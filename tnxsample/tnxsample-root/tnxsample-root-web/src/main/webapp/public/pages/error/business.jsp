<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="tnxjee" uri="/tnxjee-tags" %>
<!DOCTYPE html>
<html lang="zh-cn">
<meta charset="UTF-8">
<head>
    <title>错误</title>
</head>

<body>
<div class="offset-5 col-2 pt-5">
    <div class="alert alert-danger">
        <table>
            <tr>
                <td width="1" nowrap="nowrap" style="vertical-align: top;">
                    <h2 class="mb-0 mr-1">
                        <i class="fa fa-exclamation-circle" aria-hidden="true"></i>
                    </h2>
                </td>
                <td>
                    <tnxjee:errors/>
                </td>
            </tr>
        </table>
    </div>
    <div class="text-center">
        <button type="button" class="btn btn-primary" onclick="history.back()">返回</button>
    </div>
</div>
</body>
</html>