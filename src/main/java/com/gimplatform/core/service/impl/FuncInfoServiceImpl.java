package com.gimplatform.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.common.GlobalVal;
import com.gimplatform.core.entity.FuncInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.repository.FuncInfoRepository;
import com.gimplatform.core.repository.RoleInfoRepository;
import com.gimplatform.core.repository.TenantsInfoRepository;
import com.gimplatform.core.service.FuncInfoService;
import com.gimplatform.core.tree.Tree;
import com.gimplatform.core.tree.TreeNode;
import com.gimplatform.core.tree.TreeNodeExtend;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;
import com.gimplatform.core.utils.JsonUtils;

/**
 * 权限信息服务类
 * @author zzd
 */
@Service
public class FuncInfoServiceImpl implements FuncInfoService {

    private static final Logger logger = LogManager.getLogger(FuncInfoServiceImpl.class);

    @Autowired
    private FuncInfoRepository funcInfoRepository;

    @Autowired
    private RoleInfoRepository roleInfoRepository;

    @Autowired
    private TenantsInfoRepository tenantsInfoRepository;

    @Override
    public boolean loadFuncDataToCache() {
        List<FuncInfo> allFuncList = funcInfoRepository.findAll();
        if (allFuncList == null || allFuncList.size() == 0)
            return false;
        else {
            // 权限列表写入内存中
            GlobalVal.funcInfoList = allFuncList;
            logger.info("将权限数据写入内存中");
            return true;
        }
    }

    @Override
    public List<FuncInfo> getUserFunc(UserInfo userInfo) {
        return funcInfoRepository.getUserFunc(userInfo.getUserId(), userInfo.getOrganizerId());
        // List<FuncInfo> userFuncList = funcInfoRepository.getUserFunc(userInfo.getUserId());
        // List<FuncInfo> userOrganizerFuncList = funcInfoRepository.getUserOrganizerFunc(userInfo.getOrganizerId());
        // //去重
        // List<FuncInfo> compareList = new ArrayList<FuncInfo>();
        // compareList.addAll(userFuncList);
        // compareList.addAll(userOrganizerFuncList);
        // Set<FuncInfo> set = new TreeSet<FuncInfo>(new Comparator<FuncInfo>(){
        // @Override
        // public int compare(FuncInfo o1, FuncInfo o2) {
        // return o1.getFuncId().compareTo(o2.getFuncId());
        // }
        // });
        // set.addAll(compareList);
        // return new ArrayList<FuncInfo>(set);
    }

    @Override
    public JSONObject getFuncTree() {
        List<Map<String, Object>> listFunc = funcInfoRepository.getFuncTreeList(null, null, null);
        if (listFunc == null || listFunc.isEmpty()) {
            return RestfulRetUtils.getErrorMsg("24001", "获取权限菜单树失败");
        }
        return RestfulRetUtils.getRetSuccess(getJsonTree(listFunc, null));
    }

    @Override
    public String getFuncTreeByFuncId(Long funcId) {
        List<Long> pids = new ArrayList<Long>();
        List<Long> allIds = new ArrayList<Long>();
        pids.add(funcId);
        allIds.add(funcId);
        int deep = Constants.DEFAULT_TREE_DEEP;
        while (!pids.isEmpty() && pids.size() > 0 && deep > 0) {
            List<FuncInfo> funcInfoList = funcInfoRepository.getFuncByParentIds(pids);
            pids.clear();
            for (FuncInfo func : funcInfoList) {
                pids.add(func.getFuncId());
                allIds.add(func.getFuncId());
            }
            deep--;
        }
        List<Map<String, Object>> listFunc = funcInfoRepository.getFuncTreeList(allIds, null, null);
        if (listFunc == null || listFunc.isEmpty()) {
            return "";
        }
        // 调整ID值
        for (int i = 0; i < listFunc.size(); i++) {
            listFunc.get(i).put("funcId", listFunc.get(i).get("funcId") + "_");
            listFunc.get(i).put("parentFuncId", listFunc.get(i).get("parentFuncId") + "_");
        }
        JSONObject json = new JSONObject();
        json.put("RetData", getJsonTree(listFunc, String.valueOf(funcId) + "_"));
        return JsonUtils.jsonToXml(json);
    }

    @Override
    public JSONObject getFuncIdByTenantsId(Long tenantsId) {
        List<Object> listFunc = funcInfoRepository.getFuncIdByTenantsId(tenantsId);
        List<Long> listResult = new ArrayList<Long>();
        for (Object id : listFunc) {
            listResult.add(StringUtils.toLong(id, 0L));
        }
        return RestfulRetUtils.getRetSuccess(listResult);
    }

    @Override
    public JSONObject getFuncTreeByTenantsId(Long tenantsId) {
        List<Map<String, Object>> listFunc = funcInfoRepository.getFuncByTenantsId(tenantsId);
        if (listFunc == null || listFunc.isEmpty()) {
            return RestfulRetUtils.getErrorMsg("24001", "获取权限菜单树失败");
        }
        return RestfulRetUtils.getRetSuccess(getJsonTree(listFunc, null));
    }

    @Override
    public JSONObject addFunc(FuncInfo funcInfo, UserInfo userInfo) {
        funcInfo.setIsValid(Constants.IS_VALID_VALID);
        if (funcInfoRepository.findByFuncFlagAndIsValid(funcInfo.getFuncFlag(), Constants.IS_VALID_VALID).size() > 0) {
            return RestfulRetUtils.getErrorMsg("24005", "权限标识已存在，请重新修改");
        }
        if (funcInfo.getFuncType().equals(100200L))
            funcInfo.setFuncLevel(1L);
        else if (funcInfo.getFuncType().equals(100300L))
            funcInfo.setFuncLevel(2L);
        else if (funcInfo.getFuncType().equals(100400L))
            funcInfo.setFuncLevel(3L);

        funcInfo.setCreateDate(new Date());
        funcInfo.setCreateBy(userInfo.getUserId());
        funcInfo.setModifyBy(userInfo.getUserId());
        funcInfo.setModifyDate(new Date());
        funcInfoRepository.save(funcInfo);
        return RestfulRetUtils.getRetSuccess();
    }

    @Override
    public JSONObject editFunc(FuncInfo funcInfo, UserInfo userInfo) {
        funcInfo.setIsValid(Constants.IS_VALID_VALID);
        FuncInfo funcInfoInDb = funcInfoRepository.findOne(funcInfo.getFuncId());
        if (funcInfoInDb == null) {
            return RestfulRetUtils.getErrorMsg("24006", "当前编辑的权限不存在");
        }
        if (funcInfoInDb != null && !funcInfoInDb.getFuncFlag().equals(funcInfo.getFuncFlag()) && funcInfoRepository.findByFuncFlagAndIsValid(funcInfo.getFuncFlag(), Constants.IS_VALID_VALID).size() > 0) {
            return RestfulRetUtils.getErrorMsg("24005", "权限标识已存在，请重新修改");
        }
        if (funcInfo.getFuncType().equals(100200L))
            funcInfo.setFuncLevel(1L);
        else if (funcInfo.getFuncType().equals(100300L))
            funcInfo.setFuncLevel(2L);
        else if (funcInfo.getFuncType().equals(100400L))
            funcInfo.setFuncLevel(3L);
        // 合并两个javabean
        BeanUtils.mergeBean(funcInfo, funcInfoInDb);
        funcInfoInDb.setModifyBy(userInfo.getUserId());
        funcInfoInDb.setModifyDate(new Date());
        funcInfoRepository.save(funcInfoInDb);
        return RestfulRetUtils.getRetSuccess();
    }

    @Override
    public JSONObject delFunc(String idsList, UserInfo userInfo) {
        Long funcId = StringUtils.toLong(idsList);
        FuncInfo tmpFuncInfo = funcInfoRepository.findOne(funcId);
        // 判断是否存在基础模块
        if (tmpFuncInfo.getIsBase().equals("Y")) {
            return RestfulRetUtils.getErrorMsg("24015", "需要删除的权限存在基础模块，禁止删除！");
        }
        List<Long> pids = new ArrayList<Long>();
        List<Long> allIds = new ArrayList<Long>();
        pids.add(funcId);
        allIds.add(funcId);
        int deep = Constants.DEFAULT_TREE_DEEP;
        while (!pids.isEmpty() && pids.size() > 0 && deep > 0) {
            List<FuncInfo> funcInfoList = funcInfoRepository.getFuncByParentIds(pids);
            pids.clear();
            for (FuncInfo func : funcInfoList) {
                // 判断是否存在基础模块
                if (func.getIsBase().equals("Y")) {
                    return RestfulRetUtils.getErrorMsg("24015", "需要删除的权限存在基础模块，禁止删除！");
                }
                pids.add(func.getFuncId());
                allIds.add(func.getFuncId());
            }
            deep--;
        }

        // 批量更新（设置IsValid 为N）
        if (allIds.size() > 0) {
            funcInfoRepository.delFunc(Constants.IS_VALID_INVALID, userInfo.getUserId(), new Date(), allIds);
            // 删除关联表信息
            for (Long id : allIds) {
                roleInfoRepository.delRoleFuncByFuncId(id);
                tenantsInfoRepository.delTenantsFuncByFuncId(id);
            }
        }
        return RestfulRetUtils.getRetSuccess();
    }

    /**
     * 获取json格式的树
     * @param listFunc
     * @param parentId
     * @return
     */
    public JSONArray getJsonTree(List<Map<String, Object>> listFunc, String parentId) {
        TreeNode root = new TreeNode("root", "all", null, false);
        if (parentId != null)
            root = new TreeNode(parentId, "all", null, false);
        root.setIcon("fa fa-sitemap");
        Map<String, String> mapAttr = null;
        TreeNodeExtend treeNode = null;
        String id = "", text = "", parent = "";
        Tree tree = new Tree(true);
        for (Map<String, Object> mapObj : listFunc) {
            id = MapUtils.getString(mapObj, "funcId", "");
            text = MapUtils.getString(mapObj, "funcName", "");
            parent = MapUtils.getString(mapObj, "parentFuncId", "root");
            // 当获取到的根ID不为null，则将传送过来的parentId作为rootId
            if (parentId != null && parentId.equals(id)) {
                parent = parentId.toString();
            }

            mapAttr = new HashMap<String, String>();
            String funcType = MapUtils.getString(mapObj, "funcType");
            mapAttr.put("funcType", funcType);
            mapAttr.put("funcLevel", MapUtils.getString(mapObj, "funcLevel"));
            mapAttr.put("funcLink", MapUtils.getString(mapObj, "funcLink"));
            mapAttr.put("dispOrder", MapUtils.getString(mapObj, "dispOrder"));
            mapAttr.put("funcFlag", MapUtils.getString(mapObj, "funcFlag"));
            mapAttr.put("isBase", MapUtils.getString(mapObj, "isBase"));
            mapAttr.put("isShow", MapUtils.getString(mapObj, "isShow"));
            mapAttr.put("isBlank", MapUtils.getString(mapObj, "isBlank"));
            mapAttr.put("funcIcon", MapUtils.getString(mapObj, "funcIcon"));
            mapAttr.put("funcDesc", MapUtils.getString(mapObj, "funcDesc"));

            treeNode = new TreeNodeExtend(id, text, parent, false, mapAttr);

            // 设置图标
            if ("100300".equals(funcType))
                treeNode.setIcon("fa fa-list");
            else if ("100400".equals(funcType))
                treeNode.setIcon("fa fa-key");
            // 设置根节点图标
            if (StringUtils.isBlank(parent)) {
                treeNode.setIcon("fa fa-sitemap");
            }
            tree.addNode(treeNode);
        }
        String strTree = tree.getTreeJson(tree, root);
        return JSONArray.parseArray(strTree);
    }

    @Override
    public JSONObject saveImportFunc(String funcId, JSONObject json, UserInfo userInfo) {
        String retVal = checkCanSave(json);
        if (retVal != null) {
            return RestfulRetUtils.getErrorMsg("24005", "存在相同的权限标识[" + retVal + "],请修改后重试！");
        }
        // 导入数据
        importFileToFuncObj(json, userInfo, StringUtils.toLong(funcId));
        return RestfulRetUtils.getRetSuccess();
    }

    /**
     * 检查是否存在标识
     * @param json
     * @return
     */
    private String checkCanSave(JSONObject json) {
        if (json == null)
            return null;
        JSONObject attrJson = json.getJSONObject("attributes");
        String funcFlag = attrJson.getString("funcFlag");
        if (funcInfoRepository.findByFuncFlagAndIsValid(funcFlag, Constants.IS_VALID_VALID).size() > 0) {
            return funcFlag;
        }
        if (json.containsKey("children")) {
            Object obj = json.get("children");
            if (obj instanceof Boolean) {
            } else if (obj instanceof JSONObject) {
                JSONObject tmpJson = (JSONObject) obj;
                attrJson = tmpJson.getJSONObject("attributes");
                funcFlag = attrJson.getString("funcFlag");
                if (funcInfoRepository.findByFuncFlagAndIsValid(funcFlag, Constants.IS_VALID_VALID).size() > 0) {
                    return funcFlag;
                }
            } else if (obj instanceof JSONArray) {
                JSONArray subJson = (JSONArray) obj;
                for (int i = 0; i < subJson.size(); i++) {
                    String retVal = checkCanSave(subJson.getJSONObject(i));
                    if (retVal != null)
                        return retVal;
                }
            }
        }
        return null;
    }

    /**
     * 检查导入内容
     * @param json
     * @param userInfo
     * @param parentId
     * @return
     */
    private void importFileToFuncObj(JSONObject json, UserInfo userInfo, Long parentId) {
        Long tmpParentId = saveFuncWithJson(json, userInfo, parentId);
        if (json.containsKey("children")) {
            Object obj = json.get("children");
            if (obj instanceof Boolean) {
            } else if (obj instanceof JSONObject) {
                saveFuncWithJson(json, userInfo, parentId);
            } else if (obj instanceof JSONArray) {
                JSONArray subJson = (JSONArray) obj;
                for (int i = 0; i < subJson.size(); i++) {
                    importFileToFuncObj(subJson.getJSONObject(i), userInfo, tmpParentId);
                }
            }
        }
    }

    /**
     * @param json
     * @param userInfo
     * @param parentId
     * @return
     */
    private Long saveFuncWithJson(JSONObject json, UserInfo userInfo, Long parentId) {
        FuncInfo funcInfo = new FuncInfo();
        JSONObject attrJson = json.getJSONObject("attributes");
        funcInfo.setIsValid(Constants.IS_VALID_VALID);
        funcInfo.setParentFuncId(parentId);
        funcInfo.setFuncName(json.getString("text"));
        funcInfo.setFuncType(attrJson.getLong("funcType"));
        funcInfo.setFuncLink(attrJson.getString("funcLink"));
        funcInfo.setFuncFlag(attrJson.getString("funcFlag"));
        funcInfo.setFuncIcon(attrJson.getString("funcIcon"));
        funcInfo.setDispOrder(attrJson.getLong("dispOrder"));
        funcInfo.setIsBase(attrJson.getString("isBase"));
        funcInfo.setIsShow(attrJson.getString("isShow"));
        funcInfo.setIsBlank(attrJson.getString("isBlank"));
        funcInfo.setFuncDesc(attrJson.getString("funcDesc"));
        if (funcInfo.getFuncType().equals(100200L))
            funcInfo.setFuncLevel(1L);
        else if (funcInfo.getFuncType().equals(100300L))
            funcInfo.setFuncLevel(2L);
        else if (funcInfo.getFuncType().equals(100400L))
            funcInfo.setFuncLevel(3L);
        funcInfo.setCreateDate(new Date());
        funcInfo.setCreateBy(userInfo.getUserId());
        funcInfo.setModifyBy(userInfo.getUserId());
        funcInfo.setModifyDate(new Date());
        funcInfoRepository.saveAndFlush(funcInfo);

        return funcInfo.getFuncId();
    }
}
