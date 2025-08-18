<style type="text/css">
    body {
        color: rgb(0,0,0);
        background-color: rgb(255, 255, 255);
        font-family: Verdana, Tahoma, sans-serif;
        font-size: 11px;
        overflow-y: auto;
        padding: 15px;
        line-height: 1.4;
    }

    p, li {
        font-size: 12px;
        line-height: 1.5em;
        font-family: verdana;
        margin: 8px 0 8px 0;
        font-weight: normal;
    }
        table {
        line-height: 1.4;
        width: 100%;
        font-size: 11px;
        border-collapse: separate;
        border-spacing: 1px;
        background: white;
    }
    td, .content-container {
        padding: 4px 6px;
        vertical-align: top;
        border: 1px solid #cccccc;
        background: rgb(211,211,211);
    }
    .narr_th, .row-header {
        background: #656565;
        color: white;
        border: 1px solid #cccccc;
        padding: 4px 6px;
        font-weight: bold;
        text-align: left;
    }

    h1 {
        font-size: 12pt;
        font-weight: bold;
        margin-bottom: 15px;
        margin-top: 10px;
    }

    h2 {
        font-size: 11pt;
        font-weight: bold;
        margin-top: 20px;
        margin-bottom: 10px;
    }

    h3 {
        font-size: 10pt;
        font-weight: bold;
        margin-top: 15px;
        margin-bottom: 8px;
    }

    h4 {
        font-size: 8pt;
        font-weight: bold;
    }

    tr {
        background-color: #ccc;
    }

    td {
        padding: 0.5em;
        vertical-align: top;
    }

    a {
        /*color: rgb(0, 0, 255);
      background-color: rgb(255,255,255);*/
    }
    /*div {
    width: 80%;
}*/

    .list-unstyled {
        list-style-type: none;
    }

    .list-unstyled li {
        padding-top: 5px;
    }

    .list-header {
        font-size: 12px;
        color: rgb(0, 0, 238);
    }

    .list-header:hover {
        text-decoration: underline;
    }

    .code {
        font-family: Consolas, Menlo, Monaco, Lucida Console, Liberation Mono, DejaVu Sans Mono, Bitstream Vera Sans Mono, Courier New, monospace, sans-serif;
        color: black;
        font-size: 12px;
    }

    .p-l-10 {
        padding-left: 10px;
    }

    .section {
        font-size: 10pt;
        margin-top: 20px;
        margin-bottom: 15px;
        margin-right: 0.5em;
        font-weight: 700;
    }

    .cql-keyword-new {
        color: rgb(0, 0, 255);
        font-weight: 700;
    }

    .cql-class,
    .cql-object {
        color: rgb(25, 127, 157);
    }

    .cql_keyword {
        color: rgb(127, 0, 85);
        font-weight: 700;
    }

    .cql_function {
        color: rgb(127, 0, 85);
        font-weight: 700;
    }

    .cql_attribute {
        color: rgb(127, 0, 85);
        font-weight: 700;
    }

    .cql_string {
        color: rgb(42, 0, 255);
    }

    .cql_identifier .cql_function {
        color: rgb(60, 76, 114);
    }

    hr.style13 {
        height: 10px;
        border: 0;
        box-shadow: 0 10px 10px -10px #8c8b8b inset;
        margin: 20px 0;
    }

    .treeview:hover input~label:before,
    .treeview.hover input~label:before {
        opacity: 1.0;
        -webkit-transition-duration: 0.5s;
        -moz-transition-duration: 0.5s;
        -ms-transition-duration: 0.5s;
        -o-transition-duration: 0.5s;
        transition-duration: 0.5s;
    }

    .treeview ul {
        -webkit-transition-duration: 1s;
        -moz-transition-duration: 1s;
        -ms-transition-duration: 1s;
        -o-transition-duration: 1s;
        transition-duration: 1s;
        -webkit-transition-timing-function: ease;
        -moz-transition-timing-function: ease;
        -ms-transition-timing-function: ease;
        -o-transition-timing-function: ease;
        transition-timing-function: ease;
        list-style: none;
        padding-left: 1em;
    }

    .treeview ul li span {
        -webkit-transition-property: color;
        -moz-transition-property: color;
        -ms-transition-property: color;
        -o-transition-property: color;
        transition-property: color;
        -webkit-transition-duration: 1s;
        -moz-transition-duration: 1s;
        -ms-transition-duration: 1s;
        -o-transition-duration: 1s;
        transition-duration: 1s;

    }

    .treeview input {
        display: none;
    }

    .treeview input[type=checkbox]:checked~ul {
        display: none;
    }

    .treeview input[type=checkbox]~ul {
        margin-right: 20%;
        opacity: 1;
    }

    .treeview input~label {
        cursor: pointer;
    }

    .treeview input~label:before {
        content: '';
        width: 0;
        height: 0;
        position: absolute;
        margin-left: -1em;
        margin-top: 0.25em;
        border-width: 4px;
        border-style: solid;
        border-top-color: transparent;
        border-right-color: rgb(0, 0, 238);
        border-bottom-color: rgb(0, 0, 238);
        border-left-color: transparent;
        opacity: 1.0;
        -webkit-transition-property: opacity;
        -moz-transition-property: opacity;
        -ms-transition-property: opacity;
        -o-transition-property: opacity;
        transition-property: opacity;
        -webkit-transition-duration: 1s;
        -moz-transition-duration: 1s;
        -ms-transition-duration: 1s;
        -o-transition-duration: 1s;
        transition-duration: 1s;
    }

    .treeview input:checked~label:before {
        margin-left: -0.8em;
        border-width: 5px;
        border-top-color: transparent;
        border-right-color: transparent;
        border-bottom-color: transparent;
        border-left-color: rgb(0, 0, 238);
        opacity: 1.0;
    }

    .modal-body .treeview input~label:before,
    div[data-testid="view-hr-modal"] .treeview input~label:before {
        opacity: 1.0 !important;
    }

    .modal-body .treeview input:checked~label:before,
    div[data-testid="view-hr-modal"] .treeview input:checked~label:before {
        opacity: 1.0 !important;
    }

    .h1center {
        font-size: 12pt;
        font-weight: bold;
        text-align: center;
        width: 80%;
    }

    .header_table {
        border: 1pt solid rgb(0, 0, 0);
        margin-bottom: 20px;
    }

    .narr_table {
        width: 100%;
        border: 1px solid #ddd;
    }

    .narr_tr {
        background-color: rgb(225, 225, 225);
    }

    pre {
        /* Use horizontal scroller if needed; for Firefox 2, not needed in Firefox 3 */
        white-space: pre-wrap;
        /* css-3 */
        white-space: -moz-pre-wrap !important;
        /* Mozilla, since 1999 */
        white-space: -pre-wrap;
        /* Opera 4-6 */
        white-space: -o-pre-wrap;
        /* Opera 7 */
        word-wrap: break-word;
        /* Internet Explorer 5.5+ */
        font-family: Verdana, Tahoma, sans-serif;
        font-size: 11px;
        text-align: left;
        margin: 0px 0px 0px 0px;
        padding: 0px 0px 0px 0px;
    }

    .narr_th {
        background-color: rgb(201, 201, 201);
        border-bottom: 2px solid #999;
        padding: 8px 12px;
    }

    .td_label {
        font-weight: bold;
        color: white;
    }

    .hll {
        background-color: #ffffcc
    }

    .c {
        color: #408080;
        font-style: italic
    }

    /* Comment */
    .err {
        border: 1px solid #FF0000
    }

    /* Error */
    .k {
        color: #008000;
        font-weight: bold
    }

    /* Keyword */
    .o {
        color: #666666
    }

    /* Operator */
    .cm {
        color: #408080;
        font-style: italic
    }

    /* Comment.Multiline */
    .cp {
        color: #BC7A00
    }

    /* Comment.Preproc */
    .c1 {
        color: #408080;
        font-style: italic
    }

    /* Comment.Single */
    .cs {
        color: #408080;
        font-style: italic
    }

    /* Comment.Special */
    .gd {
        color: #A00000
    }

    /* Generic.Deleted */
    .ge {
        font-style: italic
    }

    /* Generic.Emph */
    .gr {
        color: #FF0000
    }

    /* Generic.Error */
    .gh {
        color: #000080;
        font-weight: bold
    }

    /* Generic.Heading */
    .gi {
        color: #00A000
    }

    /* Generic.Inserted */
    .go {
        color: #888888
    }

    /* Generic.Output */
    .gp {
        color: #000080;
        font-weight: bold
    }

    /* Generic.Prompt */
    .gs {
        font-weight: bold
    }

    /* Generic.Strong */
    .gu {
        color: #800080;
        font-weight: bold
    }

    /* Generic.Subheading */
    .gt {
        color: #0044DD
    }

    /* Generic.Traceback */
    .kc {
        color: #008000;
        font-weight: bold
    }

    /* Keyword.Constant */
    .kd {
        color: #008000;
        font-weight: bold
    }

    /* Keyword.Declaration */
    .kn {
        color: #008000;
        font-weight: bold
    }

    /* Keyword.Namespace */
    .kp {
        color: #008000
    }

    /* Keyword.Pseudo */
    .kr {
        color: #008000;
        font-weight: bold
    }

    /* Keyword.Reserved */
    .kt {
        color: #B00040
    }

    /* Keyword.Type */
    .m {
        color: #666666
    }

    /* Literal.Number */
    .s {
        color: #BA2121
    }

    /* Literal.String */
    .na {
        color: #7D9029
    }

    /* Name.Attribute */
    .nb {
        color: #008000
    }

    /* Name.Builtin */
    .nc {
        color: #0000FF;
        font-weight: bold
    }

    /* Name.Class */
    .no {
        color: #880000
    }

    /* Name.Constant */
    .nd {
        color: #AA22FF
    }

    /* Name.Decorator */
    .ni {
        color: #999999;
        font-weight: bold
    }

    /* Name.Entity */
    .ne {
        color: #D2413A;
        font-weight: bold
    }

    /* Name.Exception */
    .nf {
        color: #0000FF
    }

    /* Name.Function */
    .nl {
        color: #A0A000
    }

    /* Name.Label */
    .nn {
        color: #0000FF;
        font-weight: bold
    }

    /* Name.Namespace */
    .nt {
        color: #008000;
        font-weight: bold
    }

    /* Name.Tag */
    .nv {
        color: #19177C
    }

    /* Name.Variable */
    .ow {
        color: #AA22FF;
        font-weight: bold
    }

    /* Operator.Word */
    .w {
        color: #bbbbbb
    }

    /* Text.Whitespace */
    .mf {
        color: #666666
    }

    /* Literal.Number.Float */
    .mh {
        color: #666666
    }

    /* Literal.Number.Hex */
    .mi {
        color: #666666
    }

    /* Literal.Number.Integer */
    .mo {
        color: #666666
    }

    /* Literal.Number.Oct */
    .sb {
        color: #BA2121
    }

    /* Literal.String.Backtick */
    .sc {
        color: #BA2121
    }

    /* Literal.String.Char */
    .sd {
        color: #BA2121;
        font-style: italic
    }

    /* Literal.String.Doc */
    .s2 {
        color: #BA2121
    }

    /* Literal.String.Double */
    .se {
        color: #BB6622;
        font-weight: bold
    }

    /* Literal.String.Escape */
    .sh {
        color: #BA2121
    }

    /* Literal.String.Heredoc */
    .si {
        color: #BB6688;
        font-weight: bold
    }

    /* Literal.String.Interpol */
    .sx {
        color: #008000
    }

    /* Literal.String.Other */
    .sr {
        color: #BB6688
    }

    /* Literal.String.Regex */
    .s1 {
        color: #BA2121
    }

    /* Literal.String.Single */
    .ss {
        color: #19177C
    }

    /* Literal.String.Symbol */
    .bp {
        color: #008000
    }

    /* Name.Builtin.Pseudo */
    .vc {
        color: #19177C
    }

    /* Name.Variable.Class */
    .vg {
        color: #19177C
    }

    /* Name.Variable.Global */
    .vi {
        color: #19177C
    }

    /* Name.Variable.Instance */
    .il {
    color: #666666
    }

    /* Literal.Number.Integer.Long */

    #nn-text {
        text-align: center;
        text-align: center;
        color: lightgrey;
        font-size: 40px;
    }

    .cql-definition-body {
        width: 950px;
        display: block;
        word-wrap: break-word;
    }

    li {
        margin: 0 0 5px 0;
    }
    .row-header {
        background-color:#656565;
        width:20%;
        padding: 0.5em;
        text-align: left;
    }

    /*
        ===============================
        Rich text editor styling
        ===============================
        */
    table.rich-text-table th,td {
        border: 1px solid #9c9c9c;
        box-sizing: border-box;
        min-width: 1em;
        padding: 6px 8px;
        position: relative;
        vertical-align: top;
    }
    table.rich-text-table th {
        background-color: #3d25140d;
    }

    ul, ol {
        margin: 10px 0;
        padding: 0 0 0 25px;
        white-space: normal;
    }

    ul {
        list-style: disc;
    }

    ol {
        list-style: decimal;
    }

    p ~ ul, p ~ ol {
        margin-top: 0;
    }
</style>