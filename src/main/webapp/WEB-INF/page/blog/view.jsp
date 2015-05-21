<%@page contentType="text/html;charset=UTF-8" %>
<%@include file="../global/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en-AU">
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">
    <link href="${pageContext.request.contextPath}/resources/styles/home.css" rel="stylesheet" type="text/css">
    <title>${article.title}</title>
</head>

<body>
    <nav class="navbar navbar-default navbar-fixed-top" style="opacity: .9" role="navigation">
        <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/">Snowy Night</a>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="https://github.com/dubuyuye/site" target="_blank">Github</a></li>
                </ul>
            </div><!-- /.navbar-collapse -->
        </div><!-- /.container-fluid -->
    </nav>


    <div class="row" style="padding-top: 70px">
        <div class="container center-block">
            <div class="col-lg-1"></div>
            <div class="col-md-12 col-lg-10">
                <h2 class="text-center">${article.title}</h2>
                <p class="lead text-center"><fmt:formatDate value="${article.releasetime}" pattern="yyyy-MM-dd"/> by ${article.user.name}</p>

                <div>
                    ${article.content}
                </div>
            </div>
        </div>
    </div>

    <div class="center text-center">
        <p>
            <a href="#">Back to top</a>
        </p>
    </div>
</body>
</html>















