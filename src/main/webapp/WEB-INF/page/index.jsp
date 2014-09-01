<%@page contentType="text/html; charset=UTF-8" %>
<%@include file="overall/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en-AU">
<head>
    <title>index</title>
</head>
<body>
    <form action="${root}/user"  method="post">
        <table>
            <tr>
                <td>page<input type="text" name="pn"/></td>
            </tr>
            <tr>
                <td>id<input type="text" name="s_id"/></td>
            </tr>
            <tr>
                <td>name<input type="text" name="s_name"/></td>
            </tr>
            <tr>
                <td>role<input type="text" name="s_role"/></td>
            </tr>
            <tr>
                <td>power<input type="text" name="s_power"/></td>
            </tr>
        </table>
        <input type="submit" value="查询">
    </form>
    <form action="${root}/user/get"  method="post">
        <table>
            <tr>
                <td>id<input type="text" name="id"/></td>
            </tr>
        </table>
        <input type="submit" value="获取">
    </form>
    <form action="${root}/user/add"  method="post" >
        <table>
            <tr>
                <td>name<input type="text" name="name"/></td>
            </tr>
            <tr>
                <td>role<input type="text" name="role"/></td>
            </tr>
            <tr>
                <td>power<input type="text" name="power"/></td>
            </tr>
        </table>
        <input type="submit" value="添加">
    </form>
    <form action="${root}/user/edit"  method="post" >
        <table>
            <tr>
                <td>id<input type="text" name="id"/></td>
            </tr>
            <tr>
                <td>name<input type="text" name="name"/></td>
            </tr>
            <tr>
                <td>role<input type="text" name="role"/></td>
            </tr>
            <tr>
                <td>power<input type="text" name="power"/></td>
            </tr>
        </table>
        <input type="submit" value="修改">
    </form>
    <form action="${root}/user/upload" method="post" enctype="multipart/form-data">
        <input type="file" name="uploadFile" />
        <input type="submit" value="上传" />
    </form>
</body>
</html>
