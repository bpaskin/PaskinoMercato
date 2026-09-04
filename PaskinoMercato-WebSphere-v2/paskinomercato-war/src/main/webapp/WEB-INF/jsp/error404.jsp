<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<div class="container" style="text-align:center; padding-top:60px;">
    <div style="font-size:80px;">&#128373;</div>
    <h1 style="font-size:48px; color:#2e7d32;">404</h1>
    <p style="font-size:18px; color:#888;">
        ${lang eq 'it' ? 'Pagina non trovata.' : 'Page not found.'}
    </p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary" style="margin-top:20px;">
        ${lang eq 'it' ? 'Torna alla home' : 'Back to home'}
    </a>
</div>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
