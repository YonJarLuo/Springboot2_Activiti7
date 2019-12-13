# SpringBoot_Activiti
SpringBoot2集成Activiti7

## 一、环境 ##
- IDEA 2019.2
- Springboot 2.0.3.RELEASE
- MybatisPlus 3.1.0
- Activiti 7.0.0.Beta2
- Swagger 2.9.2
- Lombok 1.16.22
- Druid 1.1.16
- Mysql 5.7
- JAVA 8

## 二、使用步骤 ##
1.安装IDEA的Lombok、actiBPM插件 直接联网安装即可
2.下载项目pom依赖包，修改配置文件数据库连接信息，启动项目
3.执行sql脚本创建业务表
4.浏览器访问地址：http://localhost:8080/swagger-ui.html#/

## 三、注意事项 ##
1.springboot与activit包版本有对应关系，可参考：https://activiti.gitbook.io/activiti-7-developers-guide/releases/7.0.0.beta1
2.本项目持久层使用的是mybatisPlus，它是mybatis的增强版，同时支持两种写法。并无需再另外引入mybatis相关包，否则会有冲突
3.配置文件mybatis-plus，不是mybatis
4.由于actiBPM插件停止对IDEA的支持，所以在流程图中无法设置监听器。同时在关闭项目后，设置在.bpmn文件中的参数变量会"隐藏"，
但是如果流程图部署成功后则无需担心，因为流程图数据已经存储到数据库中。
5.在配置文件processes目录下，其实只需.bpmn即可进行部署，其它图片只是提供配置参数设置参考
6.本项目activiti禁用了自带的securit，不使用它的用户管理功能。所以项目启动后只生成25张表（无用户角色相关表）

## 四、其它 ##
1.把.bpmn转成.xml，只需复制一份.bpmn然后重命名为.xml即可
2.尝试过使用.xml来部署流程图，但是并未成功。尝试的原因在于IDEA的actiBPM插件无法设置监听器，所以很多动态的设置未能实现。有其它更好的方法可留言一起探讨，共同进步！
3.写于2019.12.13,上个月开始学习activiti,对于activiti是结合业务来完成。有些地方未必是最佳的实现方式，仅供参考。
4.本项目对你若有帮助，请记得给个星🌟，谢谢！



