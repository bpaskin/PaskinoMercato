<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>

<!-- Hero Banner -->
<div class="hero">
    <c:choose>
        <c:when test="${lang eq 'en'}">
            <h1>&#127812; Your Italian Supermarket, Online</h1>
            <p>Over 1500 quality products. Delivery across Italy only.</p>
        </c:when>
        <c:otherwise>
            <h1>&#127812; Il tuo Supermercato Italiano, Online</h1>
            <p>Oltre 1500 prodotti di qualità. Consegna in tutta Italia.</p>
        </c:otherwise>
    </c:choose>
    <a href="${pageContext.request.contextPath}/catalogo" class="hero-btn">
        ${lang eq 'it' ? 'Inizia a fare la spesa' : 'Start shopping'}
    </a>
</div>

<div class="feature-row">
    <div class="feature-card">
        <div class="feature-icon">&#127829;</div>
        <h3>${lang eq 'it' ? 'Freschezza garantita' : 'Guaranteed freshness'}</h3>
        <p>${lang eq 'it' ? 'Frutta, verdura, carne e pesce freschi ogni giorno.' : 'Fresh fruit, vegetables, meat and fish every day.'}</p>
    </div>
    <div class="feature-card">
        <div class="feature-icon">&#128666;</div>
        <h3>${lang eq 'it' ? 'Consegna veloce' : 'Fast delivery'}</h3>
        <p>${lang eq 'it' ? 'Consegna in 24-48 ore in tutta Italia. Solo indirizzi italiani.' : 'Delivery in 24-48 hours across Italy. Italian addresses only.'}</p>
    </div>
    <div class="feature-card">
        <div class="feature-icon">&#127466;&#127482;</div>
        <h3>${lang eq 'it' ? 'Pagamento in Euro' : 'Payment in Euro'}</h3>
        <p>${lang eq 'it' ? 'Tutti i prezzi in Euro (€). Nessun costo nascosto.' : 'All prices in Euro (€). No hidden costs.'}</p>
    </div>
    <div class="feature-card">
        <div class="feature-icon">&#127461;&#127481;</div>
        <h3>${lang eq 'it' ? 'Prodotti italiani' : 'Italian products'}</h3>
        <p>${lang eq 'it' ? 'Il meglio del made in Italy direttamente a casa tua.' : 'The best of Italian made, delivered to your home.'}</p>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
