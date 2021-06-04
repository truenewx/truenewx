<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <c:if test="${not empty _csrf}">
        <meta name="csrf" content="${_csrf.token}" header="${_csrf.headerName}" parameter="${_csrf.parameterName}"/>
    </c:if>
    <meta name="app.context" content="${context}">
    <meta name="app.version" content="${version}">
    <title><sitemesh:write property="title"/> - tnxsample</title>
    <link href="${context}/assets/css/app.css?v=${version}" rel="stylesheet">
    <sitemesh:write property="head"></sitemesh:write>
</head>

<body>
<div class="d-flex flex-column app-container">
    <jsp:include page="../pages/header.jsp"/>
    <div class="flex-grow-1 page-container">
        <div css="<sitemesh:write property='body.css'/>" js="<sitemesh:write property='body.js'/>">
            <sitemesh:write property="body"/>
        </div>
    </div>
    <jsp:include page="../pages/footer.jsp"/>
</div>
<script src="${context}/vendor/require-2.3.6/require.min.js"
    data-main="${context}/assets/js/app.js?v=${version}" type="text/javascript"></script>
</body>
</html>