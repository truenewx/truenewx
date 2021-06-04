<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="navbar fixed-top navbar-expand-md navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="${context}/">tnxsample</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbar"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
    <c:if test="${not empty customer}">
        <div class="collapse navbar-collapse" id="navbar">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="customerDropdown" role="button"
                            data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${customer.caption}</a>
                    <div class="dropdown-menu" aria-labelledby="customerDropdown">
                        <a class="dropdown-item" href="javascript:void(0);" @click="toUpdateInfo">个人资料</a>
                        <a class="dropdown-item" href="javascript:void(0);">修改密码</a>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="${context}/logout">登出</a>
                    </div>
                </li>
            </ul>
        </div>
    </c:if>
    </div>
</header>