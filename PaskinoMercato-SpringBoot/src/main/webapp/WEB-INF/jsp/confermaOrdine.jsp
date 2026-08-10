<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>

<div class="container">
    <div class="confirmation-box">
        <div class="confirmation-icon">&#10003;</div>
        <h1 class="confirmation-title">
            ${lang eq 'it' ? 'Ordine confermato!' : 'Order confirmed!'}
        </h1>
        <p>
            ${lang eq 'it' ? 'Grazie per il tuo acquisto. Il tuo numero ordine è:' : 'Thank you for your purchase. Your order number is:'}
        </p>
        <div class="order-number">${ordine.numeroOrdine}</div>
        <p>${lang eq 'it' ? 'Riceverai una email di conferma con i dettagli del tuo ordine.' : 'You will receive a confirmation email with your order details.'}</p>

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

        <div class="confirmation-actions">
            <a href="${pageContext.request.contextPath}/ordini" class="btn btn-secondary">
                ${lang eq 'it' ? 'I miei ordini' : 'My orders'}
            </a>
            <a href="${pageContext.request.contextPath}/catalogo" class="btn btn-primary">
                ${lang eq 'it' ? 'Continua lo shopping' : 'Continue shopping'}
            </a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
