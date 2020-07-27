package com.ssrs.platform.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.User;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.FixedCodeType;
import com.ssrs.platform.bl.LogBL;
import com.ssrs.platform.code.OperateLogType;
import com.ssrs.platform.extend.CodeService;
import com.ssrs.platform.extend.item.CodeCacheProvider;
import com.ssrs.platform.extend.item.OperateLog;
import com.ssrs.platform.model.CodeModel;
import com.ssrs.platform.model.entity.Code;
import com.ssrs.platform.model.parm.CodeParm;
import com.ssrs.platform.service.ICodeService;
import com.ssrs.platform.util.Page;
import com.ssrs.platform.util.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ssrs
 */
@RestController
@RequestMapping("/api/code")
public class CodeController extends BaseController {

    @Autowired
    private ICodeService codeService;

    @Priv
    @GetMapping
    public ApiResponses<Page> list(@RequestParam Map<String, Object> paramas) {
        String searchName = (String) paramas.get("searchName");
        IPage<Code> ipage = codeService.page(new Query<Code>().getPage(paramas), Wrappers.<Code>lambdaQuery()
                .eq(Code::getParentCode, "System")
                .like(StrUtil.isNotEmpty(searchName), Code::getCodeType, searchName)
                .or()
                .like(StrUtil.isNotEmpty(searchName), Code::getCodeName, searchName)
                .orderByAsc(Code::getCodeOrder, Code::getCodeType, Code::getParentCode));
        Page page = new Page(ipage);
        List<Code> data = (List<Code>) page.getData();
        List<CodeModel> codeModelList = new ArrayList<>();
        for (Code code : data) {
            FixedCodeType fct = CodeService.getInstance().get(code.getCodeType());
            CodeModel codeModel = BeanUtil.toBean(code, CodeModel.class);
            codeModel.setFixed(fct != null);
            codeModel.setId(code.getCodeType() + code.getParentCode() + code.getCodeValue());
            if (fct != null) {
                codeModel.setAllowAddItem(fct.allowAddItem());
            } else {
                codeModel.setAllowAddItem(false);
            }
            codeModelList.add(codeModel);
        }
        page.setData(codeModelList);
        return success(page);

    }


    @Priv
    @PostMapping
    public ApiResponses<String> create(@Validated(CodeParm.Create.class) CodeParm codeParm) {
        int count = codeService.count(Wrappers.<Code>lambdaQuery().eq(Code::getCodeType, codeParm.getCodeType()).eq(Code::getParentCode, "System"));
        if (count > 1) {
            return failure("该代码项(" + codeParm.getCodeType() + ")已经存在");
        }
        Code code = codeParm.convert(Code.class);
        code.setParentCode("System");
        code.setCodeValue("System");
        code.setCodeOrder(System.currentTimeMillis());
        codeService.save(code);
        CodeCacheProvider.setCode(code); // 更新缓存
        LogBL.addOperateLog(OperateLog.ID, OperateLogType.ADD, "添加代码项：" + JSONUtil.toJsonStr(code), "添加成功", null);
        return success("保存成功");
    }

    @Priv
    @PutMapping("/{codeType:.+}")
    public ApiResponses<String> update(@PathVariable String codeType, @Validated(CodeParm.Update.class) CodeParm codeParm) {
        if (CodeService.getInstance().get(codeType) != null) {
            return failure("固定代码项不能修改");
        }
        Code oldCode = codeService.getOne(Wrappers.<Code>lambdaQuery().eq(Code::getCodeType, codeType).eq(Code::getParentCode, "System").eq(Code::getCodeValue, "System"));
        BeanUtil.copyProperties(codeParm, oldCode);
        oldCode.setCodeType(codeType);
        oldCode.setUpdateTime(LocalDateTime.now());
        oldCode.setUpdateUser(User.getUserName());
        codeService.update(oldCode, Wrappers.<Code>lambdaUpdate().eq(Code::getCodeType, codeType).eq(Code::getParentCode, "System").eq(Code::getCodeValue, "System"));
        CodeCacheProvider.setCode(oldCode); // 更新缓存
        LogBL.addOperateLog(OperateLog.ID, OperateLogType.EDIT, "修改代码项：" + JSONUtil.toJsonStr(oldCode), "添加成功", null);
        return success("修改成功");
    }

    @Priv
    @GetMapping("/{codeType}/items")
    public ApiResponses<List<CodeModel>> itemList(@PathVariable String codeType) {
        List<Code> list = codeService.list(Wrappers.<Code>lambdaQuery().eq(Code::getParentCode, codeType).orderByAsc(Code::getCodeOrder, Code::getCodeType, Code::getParentCode));
        List<CodeModel> codeModelList = new ArrayList<>();
        for (Code code : list) {
            FixedCodeType fct = CodeService.getInstance().get(code.getCodeType());
            CodeModel codeModel = BeanUtil.toBean(code, CodeModel.class);
            codeModel.setAllowAddItem(true);
            codeModel.setFixed(fct != null && fct.contains(code.getCodeValue()));
            codeModel.setId(code.getCodeType() + code.getParentCode() + code.getCodeValue());
            codeModelList.add(codeModel);
        }
        return success(codeModelList);
    }

    @Priv
    @PostMapping("/{parentCode}/items")
    public ApiResponses<String> createItem(@PathVariable String parentCode, @Validated(CodeParm.ItemCreate.class) CodeParm codeParm) {
        int count = codeService.count(Wrappers.<Code>lambdaQuery().eq(Code::getCodeType, codeParm.getCodeType()).eq(Code::getParentCode, parentCode).eq(Code::getCodeValue, codeParm.getCodeValue()));
        if (count > 1) {
            return failure("该代码项值(" + codeParm.getCodeValue() + ")已经存在");
        }
        Code code = codeParm.convert(Code.class);
        code.setCodeOrder(System.currentTimeMillis());
        codeService.save(code);
        CodeCacheProvider.setCode(code); // 更新缓存
        LogBL.addOperateLog(OperateLog.ID, OperateLogType.ADD, "添加代码项item：" + JSONUtil.toJsonStr(code), "添加成功", null);
        return success("保存成功");
    }

    @Priv
    @PutMapping("/{parentCode}/items/{oldValue:.+}")
    public ApiResponses<String> update(@PathVariable String oldValue, @PathVariable String parentCode, @Validated(CodeParm.ItemUpdate.class) CodeParm codeParm) {
        Code code = codeParm.convert(Code.class);
        code.setCodeType(parentCode);
        code.setParentCode(parentCode);
        Code oldCode = codeService.getOne(Wrappers.<Code>lambdaQuery().eq(Code::getCodeType, parentCode).eq(Code::getParentCode, parentCode).eq(Code::getCodeValue, oldValue));
        BeanUtil.copyProperties(codeParm, oldCode);
        oldCode.setUpdateTime(LocalDateTime.now());
        oldCode.setUpdateUser(User.getUserName());
        codeService.update(oldCode, Wrappers.<Code>lambdaUpdate().eq(Code::getCodeType, parentCode).eq(Code::getParentCode, parentCode).eq(Code::getCodeValue, oldValue));
        CodeCacheProvider.setCode(oldCode); // 更新缓存
        LogBL.addOperateLog(OperateLog.ID, OperateLogType.EDIT, "添加代码项item：" + JSONUtil.toJsonStr(oldCode), "修改成功", null);
        return success("修改成功");
    }

    @Priv
    @DeleteMapping("/{codes:.+}")
    public ApiResponses<String> delete(@PathVariable String codes) {
        String[] codeTypes = codes.split(",");
        for (String codeType : codeTypes) {
            FixedCodeType fct = CodeService.getInstance().get(codeType);
            if (fct != null) {
                return failure("固定代码项不允许删除！");
            }
        }
        codeService.remove(Wrappers.<Code>lambdaQuery().in(Code::getCodeType, codeTypes));
        for (String codeType : codeTypes) {
            CodeCacheProvider.removeCode(codeType);
        }
        LogBL.addOperateLog(OperateLog.ID, OperateLogType.DELETE, "删除代码项：" + codes, "删除成功", null);
        return success("删除成功");
    }

    @Priv
    @DeleteMapping("/{codeType}/items/{items:.+}")
    public ApiResponses<String> delete(@PathVariable String items, @PathVariable String codeType) {
        String[] codeValues = items.split(",");
        FixedCodeType fct = CodeService.getInstance().get(codeType);
        for (String codeValue : codeValues) {
            if (fct != null && fct.contains(codeValue)) {
                return failure("固定代码项不允许删除！");
            }
        }
        codeService.remove(Wrappers.<Code>lambdaQuery().eq(Code::getCodeType, codeType).in(Code::getCodeValue, codeValues).ne(Code::getParentCode, "System"));
        CodeCacheProvider.removeCode(codeType);
        LogBL.addOperateLog(OperateLog.ID, OperateLogType.DELETE, "删除代码项item：" + items, "删除成功", null);
        return success("删除成功");
    }
}
