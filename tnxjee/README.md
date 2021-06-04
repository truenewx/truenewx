# tnxjee
truenewx是互联网技术研发解决方案，同时也是该解决方案的创建组织名称，由团队章程、流程规范、代码规范、体系架构、技术框架、公共组件等众多部分组成。

tnxjee是truenewx的核心组成部分，其主体是技术框架，同时也包含了代码规范，并应用和体现了体系架构。

## 目的
1. 解决互联网系统中常见的技术难点
2. 为开发人员节省时间，提高开发效率
3. 限定代码为一种尽可能优雅的形态，提高代码质量，并为业务代码的可扩展性提供帮助

## 原则
1. 不包含任何特定的业务逻辑，为所有服务端Java项目提供通用的基础支持
2. 没有特有的开发方式，也并不改变Spring/SpringBoot、JPA/Hibernate的正常使用方式，采用业界常用的成熟的开发方式，熟悉Spring/SpringBoot、JPA/Hibernate的开发人员可以轻松上手
3. 不重复发明轮子，已有技术框架可解决问题的，一律使用已有的技术框架

## 特性
1. 用maven搭建，可同时作为IDEA和eclipse工程
2. 基于JDK 11，推荐使用Openj9版JDK 11
3. 基于Spring Boot，包含大量基于Spring的扩展，以及在spring上下文工厂中使用的类
4. 基于MVC模式，采用Spring MVC框架
5. 基于Spring Security安全框架，提供了便利的安全配置机制，改善了Spring Security难于使用的缺陷
6. 基于领域模型+枚举、Repo、Service、Controller、Page/前端的分层思想
7. 支持JPA/Hibernate，作为默认ORM框架，提供了大量的自定义数据类型，可实现复杂Java数据类型到简单数据库字段类型的转换，如枚举、枚举数组、复合类型List等
8. 基于hibernate-validator的字段格式校验机制，包括前端的扩展，实现了前端校验和后端校验的一致性、便利性
9. 便捷的业务异常和字段校验异常处理机制
10. 基于JUnit和Spring-test的功能更为强大的单元测试框架
11. 支持基于Spring Cloud的分布式微服务架构
12. 各种为开发人员提供便利的工具类

## 代码规范
1. [《数据库开发设计规约》](standard/database.md)
2. [《Java开发代码规约》](standard/java.md)
3. [《Web开发代码规约》](standard/web.md)
4. [《产品研发流程规约》](standard/product-flow.md)
5. [《项目研发流程规约》](standard/project-flow.md)

## 联系方式
service@truenewx.org
