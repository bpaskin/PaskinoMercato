<footer class="footer">
    <div class="footer-content">
        <div class="footer-col">
            <strong>PaskinoMercato</strong><br/>
            <c:choose>
                <c:when test="${lang eq 'en'}">
                    Your Italian supermarket, online.<br/>Delivery only in Italy.
                </c:when>
                <c:otherwise>
                    Il tuo supermercato italiano, online.<br/>Consegna solo in Italia.
                </c:otherwise>
            </c:choose>
        </div>
        <div class="footer-col">
            <strong>${lang eq 'it' ? 'Pagamento' : 'Payment'}</strong><br/>
            ${lang eq 'it' ? 'Solo in Euro (€)' : 'Euro only (€)'}<br/>
            ${lang eq 'it' ? 'Pagamento sicuro' : 'Secure payment'}
        </div>
        <div class="footer-col">
            <strong>${lang eq 'it' ? 'Consegna' : 'Delivery'}</strong><br/>
            ${lang eq 'it' ? 'Solo indirizzi italiani' : 'Italian addresses only'}<br/>
            ${lang eq 'it' ? '24-48 ore lavorative' : '24-48 working hours'}
        </div>
    </div>
    <div class="footer-bottom">
        &copy; 2024 PaskinoMercato &mdash; P.IVA IT00000000000
    </div>
    <div class="footer-server">${serverName}</div>
</footer>
</body>
</html>
