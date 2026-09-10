<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>

<div class="container container-narrow">
    <div class="auth-box">
        <div class="auth-tabs">
            <button class="auth-tab ${empty param.tab or param.tab eq 'login' ? 'active' : ''}"
                    onclick="showTab('login')">
                ${lang eq 'it' ? 'Accedi' : 'Login'}
            </button>
            <button class="auth-tab ${param.tab eq 'registra' ? 'active' : ''}"
                    onclick="showTab('registra')">
                ${lang eq 'it' ? 'Registrati' : 'Register'}
            </button>
        </div>

        <c:if test="${not empty errore}">
            <div class="alert alert-error">${errore}</div>
        </c:if>

        <!-- LOGIN TAB -->
        <div id="tab-login" class="auth-tab-content ${empty param.tab or param.tab eq 'login' ? '' : 'hidden'}">
            <form action="${pageContext.request.contextPath}/login" method="post">
                <input type="hidden" name="azione" value="login"/>
                <input type="hidden" name="lang"   value="${lang}"/>
                <input type="hidden" name="redirect" value="${param.redirect}"/>

                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" class="form-control" required
                           placeholder="nome@esempio.it"/>
                </div>
                <div class="form-group">
                    <label>${lang eq 'it' ? 'Password' : 'Password'}</label>
                    <input type="password" name="password" class="form-control" required/>
                </div>
                <button type="submit" class="btn btn-primary btn-block">
                    ${lang eq 'it' ? 'Accedi' : 'Login'}
                </button>
            </form>
        </div>

        <!-- REGISTRATION TAB -->
        <div id="tab-registra" class="auth-tab-content ${param.tab eq 'registra' ? '' : 'hidden'}">
            <form action="${pageContext.request.contextPath}/login" method="post">
                <input type="hidden" name="azione"   value="registra"/>
                <input type="hidden" name="redirect" value="${param.redirect}"/>

                <div class="form-row">
                    <div class="form-group half">
                        <label>${lang eq 'it' ? 'Nome' : 'First name'}</label>
                        <input type="text" name="nome" class="form-control" required/>
                    </div>
                    <div class="form-group half">
                        <label>${lang eq 'it' ? 'Cognome' : 'Last name'}</label>
                        <input type="text" name="cognome" class="form-control" required/>
                    </div>
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" class="form-control" required/>
                </div>
                <div class="form-group">
                    <label>${lang eq 'it' ? 'Telefono (opzionale)' : 'Phone (optional)'}</label>
                    <input type="tel" name="telefono" class="form-control"/>
                </div>
                <div class="form-group">
                    <label>${lang eq 'it' ? 'Password' : 'Password'}</label>
                    <input type="password" name="password" class="form-control" required minlength="8"/>
                </div>
                <div class="form-group">
                    <label>${lang eq 'it' ? 'Lingua preferita' : 'Preferred language'}</label>
                    <select name="lingua" class="form-control">
                        <option value="it" ${lang eq 'it' ? 'selected' : ''}>Italiano</option>
                        <option value="en" ${lang eq 'en' ? 'selected' : ''}>English</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary btn-block">
                    ${lang eq 'it' ? 'Crea account' : 'Create account'}
                </button>
            </form>
        </div>
    </div>
</div>

<script>
function showTab(name) {
    var tabs = document.querySelectorAll('.auth-tab-content');
    for (var i=0;i<tabs.length;i++) tabs[i].classList.add('hidden');
    var btns = document.querySelectorAll('.auth-tab');
    for (var i=0;i<btns.length;i++) btns[i].classList.remove('active');
    document.getElementById('tab-'+name).classList.remove('hidden');
    event.target.classList.add('active');
}
</script>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
