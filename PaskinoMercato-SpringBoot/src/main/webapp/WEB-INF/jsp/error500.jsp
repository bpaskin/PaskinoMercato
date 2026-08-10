<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>
<div class="container">
    <h1>500 - ${lang eq 'it' ? 'Errore del server' : 'Server error'}</h1>
    <p>${lang eq 'it' ? 'Si è verificato un errore. Riprova più tardi.' : 'An error occurred. Please try again later.'}</p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
        ${lang eq 'it' ? 'Torna alla home' : 'Back to home'}
    </a>
</div>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
