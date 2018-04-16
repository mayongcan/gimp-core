<#assign className = table.className>
<#assign classNameLower = className?uncap_first>
<#assign shortName = table.shortName>
var g_params = {}, g_iframeIndex = null;
<#-- 定义文件上传和图片上传全局变量 -->
<#list table.columns as column>
<#if column.edit>
<#if column.editType = "5">
var g_filePath = null, g_fileSize = 0;
<#elseif column.editType = "6">
var g_imagePath = null
</#if>
</#if>
</#list>

<#-- *************************************************************************** -->
<#-- ******************************** 输出列表js ********************************* -->
<#-- *************************************************************************** -->
<#if pageType = "1">
$(function () {
	g_iframeIndex = parent.layer.getFrameIndex(window.name);
	formValidate();
	//取消按钮
	$("#layerCancel").click(function () {
		parent.layer.close(g_iframeIndex);
    });
});

/**
 * 获取从父窗口传送过来的值
 * @param value
 */
function receiveParams(value){
	g_params = value;
	//初始化界面
	initView();
}

/**
 * 初始化界面
 */
function initView(){
	<#-- 初始化编辑框 -->
	<#list table.columns as column>
	<#if column.edit>
    <#if column.editType = "3">
	$('#div${column.columnName}').datetimepicker({locale: 'zh-CN',format: 'YYYY-MM-DD'});
    <#elseif column.editType = "4">
	$('#div${column.columnName}').datetimepicker({locale: 'zh-CN',format: 'YYYY-MM-DD HH:mm:ss'});
    <#elseif column.editType = "5">
	$('#${column.columnNameFirstLower}').prettyFile({text:"请选择文件"});
    <#elseif column.editType = "6">
	$('#${column.columnNameFirstLower}').prettyFile({text:"请选择图片"});
    <#elseif column.editType = "7">
    //var ${column.columnNameFirstLower}Dict = top.app.getDictDataByDictTypeValue('${column.editDict}');
	top.app.addComboBoxOption($("#${column.columnNameFirstLower}"), g_params.${column.columnNameFirstLower}Dict);
    <#elseif column.editType = "8">
	//初始化ckeditor
	CKEDITOR.replace('${column.columnNameFirstLower}EditorContent',{
		filebrowserImageUploadUrl: top.app.conf.url.res.uploadCKEditorImage,
		filebrowserUploadUrl: top.app.conf.url.res.uploadCKEditorFile
	});
	</#if>
	</#if>
	</#list>
	//判断是新增还是修改
	if(g_params.type == "edit"){
		<#list table.columns as column>
		<#-- 输出编辑项 -->
		<#if column.edit>
		<#if column.editType = "5">
	    <#elseif column.editType = "6">
	    <#elseif column.editType = "8">
	    CKEDITOR.instances.${column.columnNameFirstLower}EditorContent.setData(g_params.rows.${column.columnNameFirstLower});
	    <#else>
		$('#${column.columnNameFirstLower}').val(g_params.rows.${column.columnNameFirstLower});
		</#if>
		</#if>
		</#list>
		
		<#-- 初始化编辑框 -->
		<#list table.columns as column>
		<#if column.edit>
	    <#if column.editType = "5">
		$('#${column.columnNameFirstLower}').prettyFile({text:"请选择文件", placeholder:"若不需要修改，请留空"});
	    <#elseif column.editType = "6">
		$('#${column.columnNameFirstLower}').prettyFile({text:"请选择图片", placeholder:"若不需要修改，请留空"});
		</#if>
		</#if>
		</#list>
	}
	//刷新数据，否则下拉框显示不出内容
	$('.selectpicker').selectpicker('refresh');
}

/**
 * 表单验证
 */
function formValidate(){
	$("#divEditForm").validate({
        rules: {
        	<#list table.columns as column>
    		<#if column.vaildata>
    			<#if column.vaildataRule = "1">
    		${column.columnNameFirstLower}: {required: true},
    			<#elseif column.vaildataRule = "2">
    		${column.columnNameFirstLower}: {number:true},
    			<#elseif column.vaildataRule = "3">
    		${column.columnNameFirstLower}: {letter: true},
    			<#elseif column.vaildataRule = "4">
            ${column.columnNameFirstLower}: {isZipCode: true},
    			<#elseif column.vaildataRule = "5">
        	${column.columnNameFirstLower}: {isMobile: true},
    			<#elseif column.vaildataRule = "6">
        	${column.columnNameFirstLower}: {isPhone: true},
				<#elseif column.vaildataRule = "7">
        	${column.columnNameFirstLower}: {email: true },
				<#elseif column.vaildataRule = "8">
        	${column.columnNameFirstLower}: {isIdCardNo: true},
				<#elseif column.vaildataRule = "9">
        	${column.columnNameFirstLower}: {url:true},
				<#elseif column.vaildataRule = "10">
        	${column.columnNameFirstLower}: {dateISO:true},
    			</#if>
    		</#if>
    		</#list>
        },
        messages: {
        	
        },
        //重写showErrors
        showErrors: function (errorMap, errorList) {
            $.each(errorList, function (i, v) {
                //在此处用了layer的方法
                layer.tips(v.message, v.element, { tips: [1, '#3595CC'], time: 2000 });
                return false;
            });  
        },
        //失去焦点时不验证
        onfocusout: false,
        submitHandler: function () {
        	<#-- 判断提交函数 -->
        	<#if table.ajaxUploadType = "1">
        	ajaxUploadFile();
        	<#elseif table.ajaxUploadType = "2">
        	ajaxUploadImage()
        	<#else>
        	submitAction();
        	</#if>
        }
    });
}

/**
 * 提交数据
 */
function submitAction(){
	//定义提交数据
	var submitData = {};
	if(g_params.type == "edit")
		submitData["${table.pkColumn.columnNameFirstLower}"] = g_params.rows.${table.pkColumn.columnNameFirstLower};
		
	<#-- 需要单独处理文件上传和图片上传 -->
	<#list table.columns as column>
	<#if column.edit>
	<#if column.editType = "5">
	if(g_filePath != null && g_filePath != undefined)
		submitData["${column.columnNameFirstLower}"] = g_filePath;
	<#elseif column.editType = "6">
	if(g_imagePath != null && g_imagePath != undefined)
		submitData["${column.columnNameFirstLower}"] = g_imagePath;
	<#elseif column.editType = "8">
	submitData["${column.columnNameFirstLower}"] = CKEDITOR.instances.${column.columnNameFirstLower}EditorContent.getData();;
	<#else>
	submitData["${column.columnNameFirstLower}"] = $("#${column.columnNameFirstLower}").val();
	</#if>
	</#if>
	</#list>
	//异步处理
	$.ajax({
		url: g_params.operUrl + "?access_token=" + top.app.cookies.getCookiesToken(),
	    method: 'POST',
		data:JSON.stringify(submitData),
		contentType: "application/json",
		success: function(data){
			top.app.message.loadingClose();
			if(top.app.message.code.success == data.RetCode){
				//关闭页面前设置结果
				parent.app.layer.editLayerRet = true;
	   			top.app.message.notice("数据保存成功！");
				parent.layer.close(g_iframeIndex);
	   		}else{
	   			top.app.message.error(data.RetMsg);
	   		}
        }
	});
}

<#-- *************************************************************************** -->
<#-- ******************************** 输出树js ********************************** -->
<#-- *************************************************************************** -->
<#elseif pageType = "2">
var g_comboBoxTree = null;

$(function () {
	g_iframeIndex = parent.layer.getFrameIndex(window.name);
	formValidate();
	$("#layerCancel").click(function () {
		parent.layer.close(g_iframeIndex);
    });
});

/**
 * 获取从父窗口传送过来的值
 * @param value
 */
function receiveParams(value){
	g_params = value;
	//初始化树
	initTree();
	//初始化界面
	initView();
}

/**
 * 初始化树
 */
function initTree(){
	//创建下拉树菜单
	g_comboBoxTree = AppCombotree.createNew();
	g_comboBoxTree.init($('#parentNode') , g_params.allTreeData);
}

/**
 * 初始化界面
 */
function initView(){//判断是新增还是修改
	<#-- 初始化编辑框 -->
	<#list table.columns as column>
	<#if column.edit>
    <#if column.editType = "3">
	$('#div${column.columnName}').datetimepicker({locale: 'zh-CN',format: 'YYYY-MM-DD'});
    <#elseif column.editType = "4">
	$('#div${column.columnName}').datetimepicker({locale: 'zh-CN',format: 'YYYY-MM-DD HH:mm:ss'});
    <#elseif column.editType = "5">
	$('#${column.columnNameFirstLower}').prettyFile({text:"请选择文件"});
    <#elseif column.editType = "6">
	$('#${column.columnNameFirstLower}').prettyFile({text:"请选择图片"});
    <#elseif column.editType = "7">
    var ${column.columnNameFirstLower}Dict = top.app.getDictDataByDictTypeValue('${column.editDict}');
	top.app.addComboBoxOption($("#${column.columnNameFirstLower}"), ${column.columnNameFirstLower}Dict);
    <#elseif column.editType = "8">
	//初始化ckeditor
	CKEDITOR.replace('${column.columnNameFirstLower}EditorContent',{
		filebrowserImageUploadUrl: top.app.conf.url.res.uploadCKEditorImage,
		filebrowserUploadUrl: top.app.conf.url.res.uploadCKEditorFile
	});
	</#if>
	</#if>
	</#list>
	if(g_params.type == "edit"){
		g_comboBoxTree.setValue(g_params.parentNode);

		<#list table.columns as column>
		<#-- 输出编辑项 -->
		<#if column.edit>
		<#if column.editType = "5">
	    <#elseif column.editType = "6">
	    <#elseif column.editType = "8">
	    CKEDITOR.instances.${column.columnNameFirstLower}EditorContent.setData(g_params.node.original.attributes.${column.columnNameFirstLower} == undefined ? "" : g_params.node.original.attributes.${column.columnNameFirstLower});
	    <#else>
		$('#${column.columnNameFirstLower}').val(g_params.node.original.attributes.${column.columnNameFirstLower} == undefined ? "" : g_params.node.original.attributes.${column.columnNameFirstLower});
		</#if>
		</#if>
		</#list>

		<#-- 初始化编辑框 -->
		<#list table.columns as column>
		<#if column.edit>
	    <#if column.editType = "5">
		$('#${column.columnNameFirstLower}').prettyFile({text:"请选择文件", placeholder:"若不需要修改，请留空"});
	    <#elseif column.editType = "6">
		$('#${column.columnNameFirstLower}').prettyFile({text:"请选择图片", placeholder:"若不需要修改，请留空"});
		</#if>
		</#if>
		</#list>
	}else{
		g_comboBoxTree.setValue(g_params.node);
	}
	//刷新数据，否则下拉框显示不出内容
	$('.selectpicker').selectpicker('refresh');
}

/**
 * 表单验证
 */
function formValidate(){
	$("#divEditForm").validate({
        rules: {
        	<#list table.columns as column>
    		<#if column.vaildata>
    			<#if column.vaildataRule = "1">
    		${column.columnNameFirstLower}: {required: true},
    			<#elseif column.vaildataRule = "2">
    		${column.columnNameFirstLower}: {number:true},
    			<#elseif column.vaildataRule = "3">
    		${column.columnNameFirstLower}: {letter: true},
    			<#elseif column.vaildataRule = "4">
            ${column.columnNameFirstLower}: {isZipCode: true},
    			<#elseif column.vaildataRule = "5">
        	${column.columnNameFirstLower}: {isMobile: true},
    			<#elseif column.vaildataRule = "6">
        	${column.columnNameFirstLower}: {isTel: true},
				<#elseif column.vaildataRule = "7">
        	${column.columnNameFirstLower}: {email: true },
				<#elseif column.vaildataRule = "8">
        	${column.columnNameFirstLower}: {isIdCardNo: true},
				<#elseif column.vaildataRule = "9">
        	${column.columnNameFirstLower}: {url:true},
				<#elseif column.vaildataRule = "10">
        	${column.columnNameFirstLower}: {dateISO:true},
    			</#if>
    		</#if>
    		</#list>
        },
        messages: {
        },
        //重写showErrors
        showErrors: function (errorMap, errorList) {
            $.each(errorList, function (i, v) {
                //在此处用了layer的方法
                layer.tips(v.message, v.element, { tips: [1, '#3595CC'], time: 2000 });
                return false;
            });  
        },
        //失去焦点时不验证
        onfocusout: false,
        submitHandler: function () {
        	<#-- 判断提交函数 -->
        	<#if table.ajaxUploadType = "1">
        	ajaxUploadFile();
        	<#elseif table.ajaxUploadType = "2">
        	ajaxUploadImage()
        	<#else>
        	submitAction();
        	</#if>
        }
    });
}

/**
 * 提交数据
 */
function submitAction(){
	if(g_params.node != null && g_params.node != undefined && g_params.type == "edit"){
		//判断当前编辑的父ID和ID是否一致
		if(g_params.node.id == g_comboBoxTree.getNodeId()){
			top.app.message.notice("父节点不能与当前节点一致！");
			return;
		}
	}
	//定义提交数据
	var submitData = {};
	if(g_params.node != null && g_params.node != undefined && g_params.type == "edit"){
		submitData['${table.pkColumn.columnNameFirstLower}'] = g_params.node.id;
		//判断当前编辑的父ID和ID是否一致
		if(g_params.node.id == g_comboBoxTree.getNodeId()){
			top.app.message.notice("权限父节点不能与当前权限节点一致！");
			return;
		}
	}
	//添加parentId
	submitData["parentId"] = g_comboBoxTree.getNodeId();
	<#-- 需要单独处理文件上传和图片上传 -->
	<#list table.columns as column>
	<#if column.edit>
	<#if column.editType = "5">
	if(g_filePath != null && g_filePath != undefined)
		submitData["${column.columnNameFirstLower}"] = g_filePath;
	<#elseif column.editType = "6">
	if(g_imagePath != null && g_imagePath != undefined)
		submitData["${column.columnNameFirstLower}"] = g_imagePath;
	<#elseif column.editType = "8">
	submitData["${column.columnNameFirstLower}"] = CKEDITOR.instances.${column.columnNameFirstLower}EditorContent.getData();;
	<#else>
	submitData["${column.columnNameFirstLower}"] = $("#${column.columnNameFirstLower}").val();
	</#if>
	</#if>
	</#list>
	//异步处理
	$.ajax({
		url: g_params.operUrl + "?access_token=" + top.app.cookies.getCookiesToken(),
	    method: 'POST',
		data:JSON.stringify(submitData),
		contentType: "application/json",
	    dataType: "json",
		success: function(data){
			top.app.message.loadingClose();
			if(top.app.message.code.success == data.RetCode){
				//关闭页面前设置结果
				parent.app.layer.editLayerRet = true;
	   			top.app.message.notice("数据保存成功！");
				parent.layer.close(g_iframeIndex);
	   		}else{
	   			top.app.message.error(data.RetMsg);
	   		}
        }
	});
}
</#if>

<#-- *************************************************************************** -->
<#-- ******************************** 当前页内全局函数 ********************************** -->
<#-- *************************************************************************** -->
<#-- 输出文件上传和图片上传的函数 -->
<#list table.columns as column>
<#if column.edit>
<#if column.editType = "5">
function ajaxUploadFile(){
	if($("#${column.columnNameFirstLower}")[0].files[0] == null || $("#${column.columnNameFirstLower}")[0].files[0] == undefined){
		//如果是编辑内容，可以不修改,直接进入提交数据
		if(g_params.type == "edit"){
			//启动加载层
			top.app.message.loading();
   			submitAction();
			return;
		}else{
			top.app.message.notice("请选择要上传的文件！");
			return;
		}
	}
	//上传到资源服务器
	top.app.uploadFile($("#${column.columnNameFirstLower}")[0].files[0], function(data){
		g_filePath = data;
		g_fileSize = $("#${column.columnNameFirstLower}")[0].files[0].size / 1024;
		g_fileSize = g_fileSize.toFixed(2);
		//提交数据
		submitAction();
	});
}
<#elseif column.editType = "6">
function ajaxUploadImage(){
	if($("#${column.columnNameFirstLower}")[0].files[0] == null || $("#${column.columnNameFirstLower}")[0].files[0] == undefined){
		//如果是编辑内容，可以不修改图片,直接进入提交数据
		if(g_params.type == "edit"){
   			submitAction();
			return;
		}else{
			top.app.message.notice("请选择要上传的图片！");
			return;
		}
	}
	//上传图片到资源服务器
	top.app.uploadImage($("#${column.columnNameFirstLower}")[0].files[0], function(data){
		g_imagePath = data;
		//提交数据
		submitAction();
	});
}
</#if>
</#if>
</#list>