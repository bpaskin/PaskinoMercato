<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<div class="container" style="text-align:center; padding-top:60px;">
    <div style="font-size:80px;">&#9888;</div>
    <h1 style="font-size:48px; color:#f44336;">500</h1>
    <p style="font-size:18px; color:#888;">
        ${lang eq 'it' ? 'Si è verificato un errore interno.' : 'An internal error occurred.'}
    </p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary" style="margin-top:20px;">
        ${lang eq 'it' ? 'Torna alla home' : 'Back to home'}
    </a>
</div>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
