<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="tnxjee" uri="/tnxjee-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-cn">
<meta charset="UTF-8">
<head>
    <title>登录</title>
</head>

<body js="/pages/login/manager.js">
<div class="d-flex justify-content-center">
    <el-form action="${context}/login" method="post" label-position="right" label-width="auto">
        <div class="h2 text-center py-5">登录</div>
    <tnxjee:iferror>
        <el-alert title="<tnxjee:errors/>" type="error" :closable="false" show-icon></el-alert>
    </tnxjee:iferror>
<c:forEach var="parameter" items="${param}">
    <c:if test="${parameter.key != 'username' && parameter.key != 'password'}">
        <input type="hidden" id="${parameter.key}" name="${parameter.key}" value="${parameter.value}">
    </c:if>
</c:forEach>
        <el-form-item label="用户名" prop="username">
            <el-input name="username" id="username" v-model="username"
                init-value="${param.username}"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
            <el-input type="password" id="password" v-model="password"
                init-value="${param.password}"
                @keyup.enter.native="submit($event)"></el-input>
            <input type="hidden" name="password" v-model="md5Password">
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="submit($event)">登录</el-button>
        </el-form-item>
    </el-form>
</div>
</body>
</html>