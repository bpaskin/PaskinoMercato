<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>

<div class="container">
    <h1>${lang eq 'it' ? 'Checkout' : 'Checkout'}</h1>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <div class="checkout-layout">
        <!-- Delivery Address Form -->
        <div class="checkout-form">
            <h2>${lang eq 'it' ? 'Indirizzo di consegna' : 'Delivery address'}</h2>
            <p class="info-banner">&#x1F1EE;&#x1F1F9; ${lang eq 'it' ? 'La consegna è disponibile solo in Italia.' : 'Delivery is only available in Italy.'}</p>

            <form action="${pageContext.request.contextPath}/checkout" method="post">

                <!-- Existing addresses -->
                <c:if test="${not empty indirizzi}">
                    <fieldset>
                        <legend>${lang eq 'it' ? 'I tuoi indirizzi salvati' : 'Your saved addresses'}</legend>
                        <c:forEach var="ind" items="${indirizzi}">
                            <label class="radio-label">
                                <input type="radio" name="indirizzoId" value="${ind.id}"
                                       ${ind.predefinito ? 'checked' : ''}/>
                                ${ind.indirizzoCompleto}
                            </label>
                        </c:forEach>
                        <label class="radio-label">
                            <input type="radio" name="indirizzoId" value="" id="nuovoInd"/>
                            <strong>${lang eq 'it' ? 'Usa un nuovo indirizzo' : 'Use a new address'}</strong>
                        </label>
                    </fieldset>
                </c:if>

                <!-- New address fields -->
                <div id="nuovoIndirizzoFields" class="${empty indirizzi ? '' : 'hidden'}">
                    <div class="form-row">
                        <div class="form-group two-thirds">
                            <label>${lang eq 'it' ? 'Via / Viale / Piazza' : 'Street'}</label>
                            <input type="text" name="via" class="form-control"
                                   placeholder="${lang eq 'it' ? 'Via Roma' : 'Via Roma'}"/>
                        </div>
                        <div class="form-group one-third">
                            <label>${lang eq 'it' ? 'Civico' : 'Number'}</label>
                            <input type="text" name="civico" class="form-control" placeholder="1"/>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group half">
                            <label>${lang eq 'it' ? 'Città' : 'City'}</label>
                            <input type="text" name="citta" class="form-control"
                                   placeholder="${lang eq 'it' ? 'Milano' : 'Milan'}"/>
                        </div>
                        <div class="form-group quarter">
                            <label>CAP</label>
                            <input type="text" name="cap" class="form-control" placeholder="20100" maxlength="5"/>
                        </div>
                        <div class="form-group quarter">
                            <label>${lang eq 'it' ? 'Provincia' : 'Province'}</label>
                            <input type="text" name="provincia" class="form-control"
                                   placeholder="MI" maxlength="2" style="text-transform:uppercase"/>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label>${lang eq 'it' ? 'Note (opzionale)' : 'Notes (optional)'}</label>
                    <textarea name="note" rows="3" class="form-control"
                        placeholder="${lang eq 'it' ? 'Citofono, piano, istruzioni...' : 'Buzzer, floor, instructions...'}"></textarea>
                </div>

                <button type="submit" class="btn btn-primary btn-lg">
                    ${lang eq 'it' ? 'Conferma ordine' : 'Confirm order'}
                </button>
            </form>
        </div>

        <!-- Order Summary -->
        <div class="checkout-summary">
            <h2>${lang eq 'it' ? 'Riepilogo ordine' : 'Order summary'}</h2>
            <c:forEach var="item" items="${carrelloItems}">
                <div class="summary-row">
                    <span>${item.nomeProdotto} x ${item.quantita}</span>
                    <span>
                        <fmt:formatNumber value="${item.subtotale}" type="currency"
                            currencySymbol="€" maxFractionDigits="2" minFractionDigits="2"/>
                    </span>
                </div>
            </c:forEach>
            <hr/>
            <div class="summary-total">
                <strong>Totale</strong>
                <strong>
                    <fmt:formatNumber value="${totale}" type="currency"
                        currencySymbol="€" maxFractionDigits="2" minFractionDigits="2"/>
                </strong>
            </div>
        </div>
    </div>
</div>

<script>
// Show/hide new address fields based on radio selection
(function() {
    var radios = document.querySelectorAll('input[name="indirizzoId"]');
    var fields = document.getElementById('nuovoIndirizzoFields');
    if (!fields) return;
    function toggle() {
        var selected = document.querySelector('input[name="indirizzoId"]:checked');
        if (selected && selected.value === '') {
            fields.classList.remove('hidden');
        } else {
            fields.classList.add('hidden');
        }
    }
    for (var i = 0; i < radios.length; i++) {
        radios[i].addEventListener('change', toggle);
    }
})();
</script>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
