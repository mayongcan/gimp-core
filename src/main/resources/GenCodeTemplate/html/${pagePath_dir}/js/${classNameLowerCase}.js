<#assign className = table.className>
<#assign classNameLower = className?uncap_first>
<#assign shortName = table.shortName>
<#-- *************************************************************************** -->
<#-- ******************************** 输出列表js ********************************* -->
<#-- *************************************************************************** -->
<#if pageType = "1">
var $table = $('#tableList');
<#-- 定义全局字典变量 -->
<#list table.columns as column>
<#if column.valueType = "2">
var g_${column.columnNameFirstLower}Dict = null;
</#if>
</#list>

$(function () {
	//初始化字典
	initDict();
	//初始化搜索面板
	initSearchPanel();
	//初始化权限
	initFunc();
	//初始化列表信息
	initTable();
	//初始化权限功能按钮点击事件
	initFuncBtnEvent();
});

/**
 * 初始化字典
 * @returns
 */
function initDict(){
	<#-- 定义全局字典变量 -->
	<#list table.columns as column>
	<#if column.valueType = "2">
	g_${column.columnNameFirstLower}Dict = top.app.getDictDataByDictTypeValue('${column.valueTypeDict}');
	</#if>
	</#list>
}

/**
 * 初始化搜索面板
 * @returns
 */
function initSearchPanel(){
	//搜索面板高度
	appTable.searchPannelHeight = $('#searchPannel').outerHeight(true);
	<#list table.columns as column>
	<#if column.search>
	<#if column.searchType = "3">
	$('#div${column.columnName}Begin').datetimepicker({locale: 'zh-CN',format: 'YYYY-MM-DD HH:mm:ss'});
	$('#div${column.columnName}End').datetimepicker({locale: 'zh-CN',format: 'YYYY-MM-DD HH:mm:ss'});
	</#if>
	<#-- 添加搜索框字典，并初始化搜索框 -->
	<#if column.searchType = "4">
	top.app.addComboBoxOption($("#${column.columnNameFirstLower}"), g_${column.columnNameFirstLower}Dict, true);
	</#if>
	</#if>
	</#list>
}

/**
 * 初始化权限
 */
function initFunc(){
	var g_operRights = top.app.getUserRights($.utils.getUrlParam(window.location.search,"_pid"));
	$("#tableToolbar").empty();
	var htmlTable = "";
	var length = g_operRights.length;
	for (var i = 0; i < length; i++) {
		htmlTable += "<button type='button' class='btn btn-outline btn-default' id='" + g_operRights[i].funcFlag  + "' data-action-url='" + g_operRights[i].funcLink + "'>" + 
						"<i class=\""+ g_operRights[i].funcIcon + "\" aria-hidden=\"true\"></i> " + g_operRights[i].funcName + 
					 "</button>";
	}
	//添加默认权限
	htmlTable += appTable.addDefaultFuncButton();
	$("#tableToolbar").append(htmlTable);
}

/**
 * 初始化列表信息
 */
function initTable(){
	//搜索参数
	var searchParams = function (params) {
        var param = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
		    access_token: top.app.cookies.getCookiesToken(),
            size: params.limit,   						//页面大小
            page: params.offset / params.limit,  		//当前页
			<#-- 输出搜索条件 -->
            <#list table.columns as column>
			<#if column.search>
			<#if column.searchType = "3">
			${column.columnNameFirstLower}Begin: $("#${column.columnNameFirstLower}Begin").val(),
			${column.columnNameFirstLower}End: $("#${column.columnNameFirstLower}End").val(),
			<#else>
			${column.columnNameFirstLower}: $("#search${column.columnName}").val(),
			</#if>
			</#if>
			</#list>
        };
        return param;
    };
    //初始化列表
	$table.bootstrapTable({
        url: top.app.conf.url.apigateway + "${restfulPath}/getList",   		//请求后台的URL（*）
        queryParams: searchParams,											//传递参数（*）
        uniqueId: '${table.pkColumn.columnNameFirstLower}',
        onClickRow: function(row, $el){
        	appTable.setRowClickStatus($table, row, $el);
        }
    });
	//初始化Table相关信息
	appTable.initTable($table);
	
	//搜索点击事件
	$("#btnSearch").click(function () {
		$table.bootstrapTable('refresh');
    });
	$("#btnReset").click(function () {
		<#list table.columns as column>
		<#if column.search>
		<#if column.searchType = "3">
		$("#${column.columnNameFirstLower}Begin").val("");
		$("#${column.columnNameFirstLower}End").val("");
		<#else>
		$("#search${column.columnName}").val("");
		</#if>
		</#if>
		</#list>
		$('.selectpicker').selectpicker('refresh');
		$table.bootstrapTable('refresh');
    });
}

/**
 * 初始化权限功能点击事件
 */
function initFuncBtnEvent(){
	//绑定工具条事件
	$("#${table.classNameLower}Add").click(function () {
		//设置参数
		var params = {};
		params.type = 'add';
		<#list table.columns as column>
		<#if column.valueType = "2">
		params.${column.columnNameFirstLower}Dict = g_${column.columnNameFirstLower}Dict;
		</#if>
		</#list>
		params.operUrl = top.app.conf.url.apigateway + $("#${table.classNameLower}Add").data('action-url');
		top.app.layer.editLayer('新增${moduleName}', ['710px', '${table.editBoxHeight}'], '${pagePath}/${table.classNameLowerCase}-edit.html', params, function(){
   			//重新加载列表
			$table.bootstrapTable('refresh');
		});
    });
	$("#${table.classNameLower}Edit").click(function () {
		var rows = appTable.getSelectionRows($table);
		if(rows.length == 0 || rows.length > 1){
			top.app.message.alert("请选择一条数据进行编辑！");
			return;
		}
		//设置参数
		var params = {};
		params.type = 'edit';
		params.rows = rows[0];
		<#list table.columns as column>
		<#if column.valueType = "2">
		params.${column.columnNameFirstLower}Dict = g_${column.columnNameFirstLower}Dict;
		</#if>
		</#list>
		params.operUrl = top.app.conf.url.apigateway + $("#${table.classNameLower}Edit").data('action-url');
		top.app.layer.editLayer('编辑${moduleName}', ['710px', '${table.editBoxHeight}'], '${pagePath}/${table.classNameLowerCase}-edit.html', params, function(){
   			//重新加载列表
			$table.bootstrapTable('refresh');
		});
    });
	$("#${table.classNameLower}Del").click(function () {
		var rows = appTable.getSelectionRows($table);
		if(rows.length == 0 ){
			top.app.message.alert("请选择要删除的数据！");
			return;
		}
		var idsList = "";
		$.each(rows, function(i, rowData) {
			if(idsList != "") idsList = idsList + ",";
			idsList = idsList + rowData.${table.pkColumn.columnNameFirstLower};
    	});
		appTable.delData($table, $("#${table.classNameLower}Del").data('action-url'), idsList);
    });
}

<#-- 输出格式化函数 -->
<#list table.columns as column>
<#if column.valueType = "2">
function format${column.columnName}(value,row,index){
	var i = g_${column.columnNameFirstLower}Dict.length;
	while (i--) {
		if(g_${column.columnNameFirstLower}Dict[i].ID == value){
			return g_${column.columnNameFirstLower}Dict[i].NAME;
		}
	}
	return "未知";
}
</#if>
</#list>

<#-- *************************************************************************** -->
<#-- ******************************** 输出树js ********************************** -->
<#-- *************************************************************************** -->
<#elseif pageType = "2">
var $treeView = $('#treeView'), g_toolBarPanelHeight = 0, g_selectNode = null, g_allTreeData = null;
$(function () {
	//初始化权限
	initFunc();
	//初始化树列表
	initTree();
	//初始化权限功能按钮点击事件
	initFuncBtnEvent();
});

/**
 * 初始化权限
 */
function initFunc(){
	var operRights = top.app.getUserRights($.utils.getUrlParam(window.location.search,"_pid"));
	$("#treeToolbar").empty();
	var htmlTree = "";
	var length = operRights.length;
	for (var i = 0; i < length; i++) {
		htmlTree += "<button type='button' class='btn btn-outline btn-default' id='" + operRights[i].funcFlag + "' data-action-url='" + operRights[i].funcLink + "'>" + 
						"<i class=\""+ operRights[i].funcIcon + "\" aria-hidden=\"true\"></i> " + operRights[i].funcName + 
					"</button>";

	}
	//添加树列表的权限
	$("#treeToolbar").append(htmlTree);
	g_toolBarPanelHeight = $('#treeToolbar').outerHeight(true) + 55;
}

/**
 * 初始化树列表
 */
function initTree(){
	$treeView.jstree({
		'core': {
			"check_callback": true,
			'data': function (objNode, cb) {
				$.ajax({
				    url: top.app.conf.url.apigateway + "${restfulPath}/getTreeList",
				    method: 'GET',
				    data: {
				    	access_token: top.app.cookies.getCookiesToken()
				    },success: function(data){
				    	if(top.app.message.code.success == data.RetCode){
				    		g_allTreeData = data.RetData;
							cb.call(this, data.RetData);
				    	}else{
				    		top.app.message.error(data.RetMsg);
				    	}
					}
				});
			}
		},
		"plugins": ["types"],
		"types": {
			"default": {
				"icon": "fa fa-folder"
			}
		}
    });
	
	$treeView.bind("loaded.jstree", function (e, data) {
		var inst = data.instance;  
	    var rootNode = inst.get_node(e.target.firstChild.firstChild.lastChild); 
	    //隐藏虚拟节点
	    if(rootNode.id == '-1') $treeView.jstree('hide_node', rootNode);
	    else $treeView.jstree('open_node', rootNode);
	});
	
	$treeView.bind("activate_node.jstree", function (obj, e) {
	    // 获取当前节点
		g_selectNode = e.node;
		showInfo();
	});
	$treeView.bind("refresh.jstree", function (e, data) {
		// 更新选中节点
		if(g_selectNode != null){
			g_selectNode = $treeView.jstree('get_node', g_selectNode);
			showInfo();
		}
	    //隐藏虚拟节点
		var inst = data.instance;  
	    var rootNode = inst.get_node(e.target.firstChild.firstChild.lastChild); 
	    if(rootNode.id == '-1') $treeView.jstree('hide_node', rootNode);
	});
	$treeView.css("height", getHeight(g_toolBarPanelHeight));
	$('#divInfo').css("height", getHeight(g_toolBarPanelHeight - 39));
    $(window).resize(function () {
    	$treeView.css("height", getHeight(g_toolBarPanelHeight));
    	$('#divInfo').css("height", getHeight(g_toolBarPanelHeight - 39));
    });
}

/**
 * 初始化功能按钮
 */
function initFuncBtnEvent(){
	$("#${table.classNameLower}Add").click(function () {
		//设置参数
		var params = {};
		params.type = 'add';
		params.node = g_selectNode;
		params.parentNode = getSelNodeParent();
		params.allTreeData = g_allTreeData;
		params.operUrl = top.app.conf.url.apigateway + $("#${table.classNameLower}Add").data('action-url');
		top.app.layer.editLayer('新增${moduleName}', ['710px', '${table.editBoxHeight}'], '${pagePath}/${table.classNameLowerCase}-edit.html', params, function(){
   			//重新加载
			$treeView.jstree(true).refresh();
		});
    });
	$("#${table.classNameLower}Edit").click(function () {
		if(g_selectNode == null ){
			top.app.message.alert("请选择需要编辑的节点！");
			return;
		}
		//设置参数
		var params = {};
		params.type = 'edit';
		params.node = g_selectNode;
		params.parentNode = getSelNodeParent();
		params.allTreeData = g_allTreeData;
		params.operUrl = top.app.conf.url.apigateway + $("#${table.classNameLower}Edit").data('action-url');
		top.app.layer.editLayer('编辑${moduleName}', ['710px', '${table.editBoxHeight}'], '${pagePath}/${table.classNameLowerCase}-edit.html', params, function(){
   			//重新加载列表
			$treeView.jstree(true).refresh();
		});
    });
	$("#${table.classNameLower}Del").click(function () {
		if(g_selectNode == null ){
			top.app.message.alert("请选择要删除的节点！");
			return;
		}
		var operUrl = top.app.conf.url.apigateway + $("#${table.classNameLower}Del").data('action-url');
		var idsList = g_selectNode.id;
		top.app.message.confirm("确定要删除当前选中的数据？数据删除后将不可恢复！", function(){
			$.ajax({
				url: operUrl + "?access_token=" + top.app.cookies.getCookiesToken(),
			    method: 'POST',
				data: idsList,
				contentType: "application/json",
				success: function(data){
					if(top.app.message.code.success == data.RetCode){
			   			//重新加载列表
						$treeView.jstree(true).refresh();
			   			top.app.message.notice("数据删除成功！");
			   		}else{
			   			top.app.message.error(data.RetMsg);
			   		}
		        }
			});
		});
    });
}

/**
 * 获取当前选中节点的父节点信息
 * @returns
 */
function getSelNodeParent(){
	if(g_selectNode == null){
		return null;
	}else{
		var nodeId = $treeView.jstree('get_parent', g_selectNode);
		if(nodeId == '#') return null;
		else return $treeView.jstree('get_node', nodeId);
	}
}

/**
 * 获取动态高度
 * @param pannelHeight
 * @param paginationHeight
 * @returns {Number}
 */
function getHeight(pannelHeight, paginationHeight) {
	if(pannelHeight == null || pannelHeight == undefined || !$.isNumeric (pannelHeight)) 
		pannelHeight = 0;
	if(paginationHeight == null || paginationHeight == undefined || !$.isNumeric (paginationHeight)) 
		paginationHeight = 0;
    return $(window).height() - pannelHeight - paginationHeight - 26;
}

/**
 * 显示详细内容
 */
function showInfo(){
	if(g_selectNode != null){
		if(g_selectNode.original != null && g_selectNode.original != undefined && g_selectNode.original.attributes != null && g_selectNode.original.attributes != undefined){
		<#list table.columns as column>
		<#-- 输出页面显示 -->
		<#if column.display>
			$('#${column.columnNameFirstLower}').text(g_selectNode.original.attributes.${column.columnNameFirstLower} == undefined ? "" : g_selectNode.original.attributes.${column.columnNameFirstLower});
		</#if>
		</#list>
		}
	}
}
</#if>