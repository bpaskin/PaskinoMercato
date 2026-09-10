<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : 'it'}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/include/header.jsp"/>

<div class="main-layout">
    <!-- Sidebar: Categories -->
    <aside class="sidebar">
        <h3>${lang eq 'it' ? 'Categorie' : 'Categories'}</h3>
        <ul class="category-list">
            <li>
                <a href="${pageContext.request.contextPath}/catalogo"
                   class="${categoriaId == 0 ? 'active' : ''}">
                    ${lang eq 'it' ? 'Tutti i prodotti' : 'All products'}
                </a>
            </li>
            <c:forEach var="cat" items="${categorie}">
                <li>
                    <a href="${pageContext.request.contextPath}/catalogo?cat=${cat.id}"
                       class="${categoriaSelezionata.id == cat.id ? 'active' : ''}">
                        <c:choose>
                            <c:when test="${lang eq 'en'}">${cat.nomeEn}</c:when>
                            <c:otherwise>${cat.nomeIt}</c:otherwise>
                        </c:choose>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </aside>

    <!-- Product Grid -->
    <main class="catalog-main">
        <!-- Breadcrumb / title -->
        <div class="catalog-header">
            <c:choose>
                <c:when test="${not empty cercaTesto}">
                    <h1>${lang eq 'it' ? 'Risultati per' : 'Results for'}: &ldquo;${cercaTesto}&rdquo;</h1>
                </c:when>
                <c:when test="${not empty categoriaSelezionata}">
                    <h1>
                        <c:choose>
                            <c:when test="${lang eq 'en'}">${categoriaSelezionata.nomeEn}</c:when>
                            <c:otherwise>${categoriaSelezionata.nomeIt}</c:otherwise>
                        </c:choose>
                    </h1>
                </c:when>
                <c:otherwise>
                    <h1>${lang eq 'it' ? 'Tutti i prodotti' : 'All products'}</h1>
                </c:otherwise>
            </c:choose>
            <span class="product-count">
                ${totaleProdotti} ${lang eq 'it' ? 'prodotti' : 'products'}
            </span>
        </div>

        <!-- Error / info message -->
        <c:if test="${not empty errore}">
            <div class="alert alert-error">${errore}</div>
        </c:if>

        <!-- Product Cards -->
        <c:choose>
            <c:when test="${empty prodotti}">
                <div class="empty-state">
                    <p>${lang eq 'it' ? 'Nessun prodotto trovato.' : 'No products found.'}</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="product-grid">
                    <c:forEach var="p" items="${prodotti}">
                        <div class="product-card">
                            <div class="product-img-wrap">
                                <c:choose>
                                    <c:when test="${not empty p.immagine}">
                                                <img src="${pageContext.request.contextPath}/img/prodotti/${fn:replace(p.immagine,'.jpg','.svg')}"
                                                     alt="${lang eq 'en' ? p.nomeEn : p.nomeIt}" class="product-img"
                                                     onerror="this.src='${pageContext.request.contextPath}/img/prodotti/${p.immagine}'"/>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="product-img-placeholder">&#127812;</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="product-info">
                                <h3 class="product-name">
                                    <c:choose>
                                        <c:when test="${lang eq 'en'}">${p.nomeEn}</c:when>
                                        <c:otherwise>${p.nomeIt}</c:otherwise>
                                    </c:choose>
                                </h3>
                                <p class="product-unit">${p.unitaMisura}</p>
                                <div class="product-price">
                                    <fmt:formatNumber value="${p.prezzo}" type="currency"
                                        currencySymbol="€" maxFractionDigits="2" minFractionDigits="2"/>
                                </div>
                                <c:choose>
                                    <c:when test="${p.quantitaStock > 0}">
                                        <form action="${pageContext.request.contextPath}/carrello" method="post" class="add-form">
                                            <input type="hidden" name="azione"    value="aggiungi"/>
                                            <input type="hidden" name="prodottoId" value="${p.id}"/>
                                            <button type="submit" class="btn-add">
                                                ${lang eq 'it' ? 'Aggiungi' : 'Add to cart'}
                                            </button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="out-of-stock">
                                            ${lang eq 'it' ? 'Esaurito' : 'Out of stock'}
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Pagination -->
        <c:if test="${totalePagine > 1}">
            <div class="pagination">
                <c:if test="${paginaCorrente > 1}">
                    <a href="?p=${paginaCorrente - 1}&cat=${categoriaId}"
                       class="btn-page">&laquo; ${lang eq 'it' ? 'Prec' : 'Prev'}</a>
                </c:if>
                <c:forEach begin="1" end="${totalePagine}" var="i">
                    <a href="?p=${i}&cat=${categoriaId}"
                       class="btn-page ${i == paginaCorrente ? 'active' : ''}">${i}</a>
                </c:forEach>
                <c:if test="${paginaCorrente < totalePagine}">
                    <a href="?p=${paginaCorrente + 1}&cat=${categoriaId}"
                       class="btn-page">${lang eq 'it' ? 'Succ' : 'Next'} &raquo;</a>
                </c:if>
            </div>
        </c:if>
    </main>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
