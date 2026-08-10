<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<div class="container">
    <h1>404 - ${lang eq 'it' ? 'Pagina non trovata' : 'Page not found'}</h1>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
        ${lang eq 'it' ? 'Torna alla home' : 'Back to home'}
    </a>
</div>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
