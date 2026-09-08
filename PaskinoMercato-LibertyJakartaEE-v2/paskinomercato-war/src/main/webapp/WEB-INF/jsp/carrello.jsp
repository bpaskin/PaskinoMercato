<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>

<div class="container">
    <h1>${lang eq 'it' ? 'Il tuo carrello' : 'Your shopping cart'}</h1>

    <c:choose>
        <c:when test="${empty sessionScope.carrello or sessionScope.carrello.numeroArticoli == 0}">
            <div class="empty-state">
                <p>${lang eq 'it' ? 'Il tuo carrello è vuoto.' : 'Your cart is empty.'}</p>
                <a href="${pageContext.request.contextPath}/catalogo" class="btn btn-primary">
                    ${lang eq 'it' ? 'Continua a fare la spesa' : 'Continue shopping'}
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <table class="cart-table">
                <thead>
                    <tr>
                        <th>${lang eq 'it' ? 'Prodotto' : 'Product'}</th>
                        <th>${lang eq 'it' ? 'Prezzo' : 'Price'}</th>
                        <th>${lang eq 'it' ? 'Quantità' : 'Quantity'}</th>
                        <th>Subtotale</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${sessionScope.carrello.items}">
                        <tr>
                            <td>
                                <c:if test="${not empty item.immagine}">
                                    <img src="${pageContext.request.contextPath}/img/prodotti/${fn:replace(item.immagine,'.jpg','.svg')}"
                                         width="50" alt="${item.nomeProdotto}"/>
                                </c:if>
                                ${item.nomeProdotto}
                            </td>
                            <td>
                                <fmt:formatNumber value="${item.prezzoUnitario}" type="currency"
                                    currencySymbol="€" maxFractionDigits="2" minFractionDigits="2"/>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/carrello" method="post" class="inline-form">
                                    <input type="hidden" name="azione"     value="aggiorna"/>
                                    <input type="hidden" name="prodottoId" value="${item.prodottoId}"/>
                                    <input type="number" name="quantita"   value="${item.quantita}"
                                           min="1" max="99" class="qty-input"/>
                                    <button type="submit" class="btn-sm">OK</button>
                                </form>
                            </td>
                            <td>
                                <fmt:formatNumber value="${item.subtotale}" type="currency"
                                    currencySymbol="€" maxFractionDigits="2" minFractionDigits="2"/>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/carrello" method="post" class="inline-form">
                                    <input type="hidden" name="azione"     value="rimuovi"/>
                                    <input type="hidden" name="prodottoId" value="${item.prodottoId}"/>
                                    <button type="submit" class="btn-remove">&times;</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="3" class="text-right"><strong>Totale</strong></td>
                        <td colspan="2">
                            <strong>
                                <fmt:formatNumber value="${sessionScope.carrello.totale}" type="currency"
                                    currencySymbol="€" maxFractionDigits="2" minFractionDigits="2"/>
                            </strong>
                        </td>
                    </tr>
                </tfoot>
            </table>

            <div class="cart-actions">
                <form action="${pageContext.request.contextPath}/carrello" method="post" class="inline-form">
                    <input type="hidden" name="azione" value="svuota"/>
                    <button type="submit" class="btn btn-secondary">
                        ${lang eq 'it' ? 'Svuota carrello' : 'Clear cart'}
                    </button>
                </form>
                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary">
                    ${lang eq 'it' ? 'Procedi al pagamento' : 'Proceed to checkout'}
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
