let thisPage;

function make_button(symbol, index, config, disabled, active) {
    let button = document.createElement("button");
    button.innerHTML = symbol;
    button.addEventListener("click", function (event) {
        event.preventDefault();
        if (this.disabled !== true) {
            config.page = index;
            paginator(config);
        }
        return false;
    }, false);
    if (disabled) {
        button.disabled = true;
    }
    if (active) {
        button.className = config.active_class;
    }
    return button;
}

function paginator(config) {
    if (typeof config != "object")
        throw "Paginator was expecting a config object!";
    if (typeof config.get_rows != "function" && !(config.table instanceof Element))
        throw "Paginator was expecting a table or get_row function!";
    if (typeof config.disable == "undefined") {
        config.disable = false;
    }
    let box;
    if (!(config.box instanceof Element)) {
        config.box = document.createElement("div");
    }
    box = config.box;
    if (typeof config.get_rows != "function") {
        config.get_rows = function () {
            let table = config.table
            let tbody = table.getElementsByTagName("tbody")[0] || table;
            children = tbody.children;
            let trs = [];
            for (let i = 0; i < children.length; i++) {
                if (children[i].nodeType = "tr") {
                    if (children[i].getElementsByTagName("td").length > 0) {
                        trs.push(children[i]);
                    }
                }
            }
            return trs;
        }
    }
    let get_rows = config.get_rows;
    let trs = get_rows();
    if (typeof config.rows_per_page == "undefined") {
        let selects = box.getElementsByTagName("select");
        if (typeof selects != "undefined" && (selects.length > 0 && typeof selects[0].selectedIndex != "undefined")) {
            config.rows_per_page = selects[0].options[selects[0].selectedIndex].value;
        } else {
            config.rows_per_page = 10;
        }
    }
    let rows_per_page = config.rows_per_page;
    if (typeof config.page == "undefined") {
        config.page = 1;
    }
    let page = config.page;
    let pages = (rows_per_page > 0) ? Math.ceil(trs.length / rows_per_page) : 1;
    if (pages < 1) {
        pages = 1;
    }
    if (page > pages) {
        page = pages;
    }
    if (page < 1) {
        page = 1;
    }
    config.page = page;
    thisPage = page;
    // hide rows not on current page and show the rows that are
    for (let i = 0; i < trs.length; i++) {
        if (typeof trs[i]["data-display"] == "undefined") {
            trs[i]["data-display"] = trs[i].style.display || "";
        }
        if (rows_per_page > 0) {
            if (i < page * rows_per_page && i >= (page - 1) * rows_per_page) {
                trs[i].style.display = trs[i]["data-display"];
            } else {
                // Only hide if pagination is not disabled
                if (!config.disable) {
                    trs[i].style.display = "none";
                } else {
                    trs[i].style.display = trs[i]["data-display"];
                }
            }
        } else {
            trs[i].style.display = trs[i]["data-display"];
        }
    }

    // page button maker functions
    config.active_class = config.active_class || "active";
    if (typeof config.box_mode != "function" && config.box_mode != "list" && config.box_mode != "buttons") {
        config.box_mode = "button";
    }
    if (typeof config.box_mode == "function") {
        config.box_mode(config);
    } else {
        let page_box = document.createElement("div");
        let left = make_button("&laquo;", (page > 1 ? page - 1 : 1), config, (page === 1), false);
        page_box.appendChild(left);

        let start;
        let end;
        let hellipBefore = document.createElement("div");
        page_box.appendChild(hellipBefore);
        if (page > 3) {
            hellipBefore.innerHTML = "&hellip;";
        }
        if (page <= 3) {
            start = 1;
        } else if (page + 2 > pages) {
            start = pages - 4;
        } else {
            start = page - 2;
        }
        if (start < 3) {
            end = start + 4;
        } else if (page + 2 > pages) {
            end = pages;
        } else {
            end = page + 2;
        }
        for (let i = start; i <= end; i++) {
            if (i > 0 && i <= pages) {
                let li = make_button(i, i, config, false, (page === i));
                page_box.appendChild(li);
            }
        }
        let hellipAfter = document.createElement("div");
        page_box.appendChild(hellipAfter);
        if (page + 2 < pages && pages > 6) {
            hellipAfter.innerHTML = "&hellip;";
        }

        let right = make_button("&raquo;", (pages > page ? page + 1 : page), config, (page === pages), false);
        page_box.appendChild(right);
        if (box.childNodes.length) {
            while (box.childNodes.length > 1) {
                box.removeChild(box.childNodes[0]);
            }
            box.replaceChild(page_box, box.childNodes[0]);
        } else {
            box.appendChild(page_box);
        }
    }

    // make rows per page selector
    if (!(typeof config.page_options == "boolean" && !config.page_options)) {
        if (typeof config.page_options == "undefined") {
            config.page_options = [
                {value: 5, text: '5'},
                {value: 10, text: '10'},
                {value: 20, text: '20'},
                {value: 50, text: '50'},
                {value: 0, text: 'All'}
            ];
        }
        let options = config.page_options;
        let select = document.createElement("select");
        for (let i = 0; i < options.length; i++) {
            let o = document.createElement("option");
            o.value = options[i].value;
            o.text = options[i].text;
            select.appendChild(o);
        }
        select.value = rows_per_page;
        select.addEventListener("change", function () {
            config.rows_per_page = this.value;
            paginator(config);
        }, false);
        box.appendChild(select);
    }

    // status message
    let stat = document.createElement("span");
    stat.innerHTML = "Страница " + page + " из " + pages
        + ", показаны строки " + (((page - 1) * rows_per_page) + 1)
        + "-" + (trs.length < page * rows_per_page || rows_per_page == 0 ? trs.length : page * rows_per_page)
        + " из " + trs.length;
    box.appendChild(stat);

    // hide pagination if disabled
    if (config.disable) {
        if (typeof box["data-display"] == "undefined") {
            box["data-display"] = box.style.display || "";
        }
        box.style.display = "none";
    } else {
        if (box.style.display === "none") {
            box.style.display = box["data-display"] || "";
        }
    }
    return box;
}

function sortTable(n, type) {
    let table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
    table = document.getElementById("dest");
    switching = true;
    let funcAsc, funcDesc;
    if (type === 'str') {
        funcAsc = function (xx, yy) {
            return xx.innerHTML.toLowerCase() > yy.innerHTML.toLowerCase();
        }
        funcDesc = function (xx, yy) {
            return xx.innerHTML.toLowerCase() < yy.innerHTML.toLowerCase()
        }
    } else if (type === 'num') {
        funcAsc = function (xx, yy) {
            return Number(xx.innerHTML) > Number(yy.innerHTML);
        }
        funcDesc = function (xx, yy) {
            return Number(xx.innerHTML) < Number(yy.innerHTML);
        }
    }
    dir = "asc";
    while (switching) {
        switching = false;
        rows = table.getElementsByTagName("TR");
        for (i = 0; i < (rows.length - 1); i++) {
            shouldSwitch = false;
            x = rows[i].getElementsByTagName("TD")[n];
            y = rows[i + 1].getElementsByTagName("TD")[n];
            if (dir === "asc") {
                if (funcAsc(x, y)) {
                    shouldSwitch = true;
                    break;
                }
            } else if (dir === "desc") {
                if (funcDesc(x, y)) {
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch) {
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
            switchcount++;
        } else {
            if (switchcount === 0 && dir === "asc") {
                dir = "desc";
                switching = true;
            }
        }
    }
    let elements = document.querySelector('#productTable').getElementsByTagName('th');
    for (let j = 0; j < elements.length; j++) {
        elements[j].classList.remove('asc', 'desc');
    }
    let check = table.getElementsByTagName("TD")[0].classList.contains('id');
    elements[check ? n - 1 : n].classList.add(dir);
    let buttons = $('#index_native > div button.active');
    if (buttons != null && buttons.length > 0)
        buttons[0].click();
}