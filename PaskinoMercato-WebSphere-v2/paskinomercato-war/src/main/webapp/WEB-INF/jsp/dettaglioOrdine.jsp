<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>

<div class="container">
    <a href="${pageContext.request.contextPath}/ordini" class="back-link">
        &larr; ${lang eq 'it' ? 'Torna agli ordini' : 'Back to orders'}
    </a>
    <h1>${lang eq 'it' ? 'Dettaglio ordine' : 'Order detail'}</h1>

    <div class="order-meta">
        <p><strong>${lang eq 'it' ? 'Numero ordine' : 'Order number'}:</strong> ${ordine.numeroOrdine}</p>
        <p><strong>${lang eq 'it' ? 'Data' : 'Date'}:</strong>
            <fmt:formatDate value="${ordine.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
        </p>
        <p><strong>Stato:</strong>
            <c:choose>
                <c:when test="${lang eq 'en'}">
                    <c:choose>
                        <c:when test="${ordine.stato eq 'IN_ATTESA'}">Pending</c:when>
                        <c:when test="${ordine.stato eq 'CONFERMATO'}">Confirmed</c:when>
                        <c:when test="${ordine.stato eq 'IN_PREPARAZIONE'}">Preparing</c:when>
                        <c:when test="${ordine.stato eq 'SPEDITO'}">Shipped</c:when>
                        <c:when test="${ordine.stato eq 'CONSEGNATO'}">Delivered</c:when>
                        <c:when test="${ordine.stato eq 'ANNULLATO'}">Cancelled</c:when>
                        <c:otherwise>${ordine.stato}</c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>${ordine.stato.replace('_',' ')}</c:otherwise>
            </c:choose>
        </p>
    </div>

    <table class="order-table">
        <thead>
            <tr>
                <th>${lang eq 'it' ? 'Prodotto' : 'Product'}</th>
                <th>${lang eq 'it' ? 'Qtà' : 'Qty'}</th>
                <th>${lang eq 'it' ? 'Prezzo' : 'Price'}</th>
                <th>Subtotale</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="riga" items="${righe}">
                <tr>
                    <td>${riga.nomeProdotto}</td>
                    <td>${riga.quantita}</td>
                    <td>
                        <fmt:formatNumber value="${riga.prezzoUnitario}" type="currency"
                            currencySymbol="€" maxFractionDigits="2" minFractionDigits="2"/>
                    </td>
                    <td>
                        <fmt:formatNumber value="${riga.subtotale}" type="currency"
                            currencySymbol="€" maxFractionDigits="2" minFractionDigits="2"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
        <tfoot>
            <tr>
                <td colspan="3" class="text-right"><strong>Totale</strong></td>
                <td>
                    <strong>
                        <fmt:formatNumber value="${ordine.totale}" type="currency"
                            currencySymbol="€" maxFractionDigits="2" minFractionDigits="2"/>
                    </strong>
                </td>
            </tr>
        </tfoot>
    </table>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
