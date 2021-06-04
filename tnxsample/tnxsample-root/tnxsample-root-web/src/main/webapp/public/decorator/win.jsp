<%@ page language="java" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta charset="utf-8">
    <title><sitemesh:write property="title"/></title>
    <sitemesh:write property="head"/>
</head>
<body css="<sitemesh:write property='body.css'/>" js="<sitemesh:write property='body.js'/>"
    width="<sitemesh:write property='body.width'/>">
<sitemesh:write property="body"/>
</body>
</html>