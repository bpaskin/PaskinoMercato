<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>

<div class="container">
    <h1>${lang eq 'it' ? 'I miei ordini' : 'My orders'}</h1>

    <c:choose>
        <c:when test="${empty ordini}">
            <div class="empty-state">
                <p>${lang eq 'it' ? 'Non hai ancora nessun ordine.' : 'You have no orders yet.'}</p>
                <a href="${pageContext.request.contextPath}/catalogo" class="btn btn-primary">
                    ${lang eq 'it' ? 'Inizia a fare la spesa' : 'Start shopping'}
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <table class="orders-table">
                <thead>
                    <tr>
                        <th>${lang eq 'it' ? 'Numero ordine' : 'Order number'}</th>
                        <th>${lang eq 'it' ? 'Data' : 'Date'}</th>
                        <th>Totale</th>
                        <th>Stato</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="ordine" items="${ordini}">
                        <tr>
                            <td>${ordine.numeroOrdine}</td>
                            <td>
                                <fmt:formatDate value="${ordine.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                            </td>
                            <td>
                                <fmt:formatNumber value="${ordine.totale}" type="currency"
                                    currencySymbol="€" maxFractionDigits="2" minFractionDigits="2"/>
                            </td>
                            <td>
                                <span class="badge badge-${ordine.stato.toLowerCase().replace('_','-')}">
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
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/ordini?numero=${ordine.numeroOrdine}"
                                   class="btn-sm">
                                    ${lang eq 'it' ? 'Dettagli' : 'Details'}
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
