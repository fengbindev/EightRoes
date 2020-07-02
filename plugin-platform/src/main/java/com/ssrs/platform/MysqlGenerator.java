package com.ssrs.platform;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.*;

/**
 * 代码生成器
 * @author ssrs
 */
public class MysqlGenerator {


    public static void main(String[] args) {
        String path = "G:\\EightRoes\\EightRoes\\plugin-platform\\";
        String jdbc = "jdbc:mysql://127.0.0.1:3306/eight-roes?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
        String[] include = new String[]{"sys_schedule"}; // 要生成的表名
        generator(path, jdbc, include);
    }

    /**
     * <p>
     * MySQL generator
     * </p>
     */
    public static void generator(String path, String jdbc, String[] include) {
        // 自定义需要填充的字段
        List<TableFill> tableFillList = new ArrayList<>();
        tableFillList.add(new TableFill("create_user", FieldFill.INSERT));
        tableFillList.add(new TableFill("create_time", FieldFill.INSERT));
        tableFillList.add(new TableFill("update_user", FieldFill.UPDATE));
        tableFillList.add(new TableFill("update_time", FieldFill.UPDATE));
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator().setGlobalConfig(
                // 全局配置
                new GlobalConfig()
                        .setOutputDir(path + "src\\main\\java")//输出目录
                        .setFileOverride(false)// 是否覆盖文件
                        .setActiveRecord(false)// 开启 activeRecord 模式
                        .setEnableCache(false)// XML 二级缓存
                        .setBaseResultMap(false)// XML ResultMap
                        .setBaseColumnList(false)// XML columList
                        .setKotlin(false) //是否生成 kotlin 代码
                        .setAuthor("ssrs") //作者
                        //自定义文件命名，注意 %s 会自动填充表实体属性！
                        .setEntityName("%s")
                        .setMapperName("%sMapper")
                        .setXmlName("%sMapper")
                        .setServiceName("I%sService")
                        .setServiceImplName("%sServiceImpl")
                        .setControllerName("%sController")
        ).setDataSource(
                // 数据源配置
                new DataSourceConfig()
                        .setDbType(DbType.MYSQL)// 数据库类型
                        .setTypeConvert(new MySqlTypeConvert() {
                            @Override
                            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {

                                if (fieldType.toLowerCase().contains("bit")) {
                                    return DbColumnType.BOOLEAN;
                                }
                                if (fieldType.toLowerCase().contains("tinyint")) {
                                    return DbColumnType.BOOLEAN;
                                }
                                if (fieldType.toLowerCase().contains("datetime")) {
                                    return DbColumnType.LOCAL_DATE_TIME;
                                }
                                if (fieldType.toLowerCase().contains("date")) {
                                    return DbColumnType.LOCAL_DATE;
                                }
                                if (fieldType.toLowerCase().contains("time")) {
                                    return DbColumnType.LOCAL_TIME;
                                }
                                return super.processTypeConvert(globalConfig, fieldType);
                            }
                        })
                        .setDriverName("com.mysql.cj.jdbc.Driver")
                        .setUsername("root")
                        .setPassword("1234")
                        .setUrl(jdbc)
        ).setStrategy(
                // 策略配置
                new StrategyConfig()
                        .setRestControllerStyle(true)
                        .setCapitalMode(false)// 全局大写命名
                        .setTablePrefix("sys_")// 去除前缀
                        .setNaming(NamingStrategy.underline_to_camel)// 表名生成策略
                        .setInclude(include) // 需要生成的表
                        // 自动填充字段
                        .setTableFillList(tableFillList)
                        // 【实体】是否生成字段常量（默认 false）
                        .setEntityColumnConstant(true)
                        // 【实体】是否为构建者模型（默认 false）
                        .setEntityBuilderModel(false)
                        // 【实体】是否为lombok模型（默认 false）<a href="https://projectlombok.org/">document</a>
                        .setEntityLombokModel(true)
                        // Boolean类型字段是否移除is前缀处理
                        .setEntityBooleanColumnRemoveIsPrefix(true)
                        .setRestControllerStyle(true)
                // .setControllerMappingHyphenStyle(true)
        ).setCfg(
                // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
                new InjectionConfig() {
                    @Override
                    public void initMap() {
                        Map<String, Object> map = new HashMap<>();
                        this.setMap(map);
                    }
                }.setFileOutConfigList(Collections.<FileOutConfig>singletonList(new FileOutConfig(
                        "/templates/mapper.xml.ftl") {
                    // 自定义输出文件目录
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return path + "src\\main\\resources\\mapper\\" + tableInfo.getEntityName() + "Mapper.xml";
                    }
                }))
        ).setPackageInfo(
                // 包配置
                new PackageConfig()
                        .setParent("com.ssrs.platform")
                        .setController("controller")
                        .setEntity("model.entity")
                        .setMapper("mapper")
                        .setService("service")
                        .setServiceImpl("service.impl")
        ).setTemplate(
                new TemplateConfig().setXml(null)
        );
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }


}
