<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}" scope="request"/>
<html lang="${lang}">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>PaskinoMercato</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">
        <a href="${pageContext.request.contextPath}/">
            <span class="brand-icon">&#127812;</span> PaskinoMercato
        </a>
    </div>
    <div class="nav-search">
        <form action="${pageContext.request.contextPath}/catalogo" method="get">
            <input type="text" name="cerca" placeholder="${lang eq 'it' ? 'Cerca prodotti...' : 'Search products...'}"
                   value="${param.cerca}" class="search-input"/>
            <button type="submit" class="btn-search">
                ${lang eq 'it' ? 'Cerca' : 'Search'}
            </button>
        </form>
    </div>
    <div class="nav-actions">
        <!-- Language switcher -->
        <a href="${pageContext.request.contextPath}/lingua?lang=it"
           class="btn-lang ${lang eq 'it' ? 'active' : ''}">IT</a>
        <a href="${pageContext.request.contextPath}/lingua?lang=en"
           class="btn-lang ${lang eq 'en' ? 'active' : ''}">EN</a>

        <!-- Cart -->
        <a href="${pageContext.request.contextPath}/carrello" class="btn-cart">
            &#128722;
            <c:if test="${not empty sessionScope.carrello}">
                <span class="cart-badge">${sessionScope.carrello.numeroArticoli}</span>
            </c:if>
        </a>

        <!-- Login / Account -->
        <c:choose>
            <c:when test="${not empty sessionScope.cliente}">
                <span class="nav-user">
                    ${sessionScope.cliente.nome}
                    | <a href="${pageContext.request.contextPath}/ordini">
                        ${lang eq 'it' ? 'Ordini' : 'Orders'}
                      </a>
                    | <a href="${pageContext.request.contextPath}/login?azione=logout">
                        ${lang eq 'it' ? 'Esci' : 'Logout'}
                      </a>
                </span>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login" class="btn-login">
                    ${lang eq 'it' ? 'Accedi' : 'Login'}
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>
