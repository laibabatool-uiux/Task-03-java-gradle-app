package com.task03.app.web;

import com.task03.app.data.BuildInfoService;

import java.util.LinkedHashMap;
import java.util.Map;

/** Renders the shared page shell: title block, navigation and stylesheet. */
public final class Layout {

    private static final Map<String, String> NAV = new LinkedHashMap<>();

    static {
        NAV.put("/", "Overview");
        NAV.put("/pipeline", "Pipeline");
        NAV.put("/build", "Build");
        NAV.put("/dependencies", "Dependencies");
        NAV.put("/api", "API");
        NAV.put("/about", "About");
    }

    private Layout() {
    }

    public static String render(String activePath, String title, String lede, String body) {
        BuildInfoService info = BuildInfoService.INSTANCE;

        StringBuilder nav = new StringBuilder();
        for (Map.Entry<String, String> e : NAV.entrySet()) {
            boolean active = e.getKey().equals(activePath);
            nav.append("<a href=\"").append(e.getKey()).append("\"")
               .append(active ? " class=\"on\" aria-current=\"page\"" : "")
               .append(">").append(e.getValue()).append("</a>");
        }

        return "<!doctype html>\n<html lang=\"en\">\n<head>\n"
            + "<meta charset=\"utf-8\">\n"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
            + "<title>" + esc(title) + " \u2014 Task-03</title>\n"
            + "<style>" + CSS + "</style>\n</head>\n<body>\n"
            + "<a class=\"skip\" href=\"#main\">Skip to content</a>\n"
            + "<aside class=\"rail\">\n"
            + "  <div class=\"mark\"><span>Task</span><b>03</b></div>\n"
            + "  <p class=\"tagline\">A Java service built by Gradle and delivered by Jenkins.</p>\n"
            + "  <nav aria-label=\"Sections\">" + nav + "</nav>\n"
            + "  <dl class=\"block\">\n"
            + "    <div><dt>Release</dt><dd>" + esc(info.appVersion()) + "</dd></div>\n"
            + "    <div><dt>Runtime</dt><dd>Java " + esc(info.javaVersion()) + "</dd></div>\n"
            + "    <div><dt>Host</dt><dd>" + esc(info.hostname()) + "</dd></div>\n"
            + "    <div><dt>Port</dt><dd>" + info.port() + "</dd></div>\n"
            + "  </dl>\n"
            + "</aside>\n"
            + "<main id=\"main\">\n"
            + "  <header class=\"head\">\n"
            + "    <h1>" + esc(title) + "</h1>\n"
            + "    <p class=\"lede\">" + esc(lede) + "</p>\n"
            + "  </header>\n"
            + body
            + "\n  <footer class=\"foot\">\n"
            + "    <span>Serving for " + esc(info.uptime()) + "</span>\n"
            + "    <span>" + esc(info.osName()) + " on " + esc(info.osArch()) + "</span>\n"
            + "  </footer>\n"
            + "</main>\n</body>\n</html>";
    }

    /** Escapes text before it is placed into HTML. */
    public static String esc(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;");
    }

    private static final String CSS = """
      *,*::before,*::after{box-sizing:border-box}
      :root{
        --ink:#14213A; --ink-2:#41506E; --ink-3:#6B788F;
        --sheet:#F4F7FA; --white:#FFFFFF;
        --rule:#B9C4D4; --rule-2:#DAE1EA;
        --live:#0B6E5F; --alert:#8C2F1E;
        --mono:ui-monospace,SFMono-Regular,"SF Mono",Menlo,Consolas,monospace;
        --sans:ui-sans-serif,system-ui,"Segoe UI",Roboto,Helvetica,Arial,sans-serif;
      }
      html{-webkit-text-size-adjust:100%}
      body{
        margin:0; background:var(--sheet); color:var(--ink);
        font-family:var(--sans); font-size:16px; line-height:1.6;
        display:grid; grid-template-columns:15.5rem minmax(0,1fr);
      }
      .skip{position:absolute;left:-999px}
      .skip:focus{left:.5rem;top:.5rem;background:var(--ink);color:#fff;padding:.5rem .75rem;z-index:9}
      a{color:var(--ink)}
      :focus-visible{outline:2px solid var(--live);outline-offset:2px}

      /* ---- title block ---- */
      .rail{
        position:sticky; top:0; align-self:start; height:100vh;
        padding:2rem 1.5rem; border-right:1px solid var(--rule);
        background:
          linear-gradient(var(--rule-2) 1px,transparent 1px) 0 0/100% 1.5rem,
          var(--white);
        display:flex; flex-direction:column; gap:1.75rem;
      }
      .mark{font-family:var(--mono);font-size:1.35rem;letter-spacing:-.02em;line-height:1}
      .mark span{color:var(--ink-3)}
      .mark b{color:var(--ink)}
      .tagline{margin:-1.1rem 0 0;font-size:.82rem;line-height:1.5;color:var(--ink-3);max-width:22ch}
      .rail nav{display:flex;flex-direction:column;border-top:1px solid var(--rule)}
      .rail nav a{
        padding:.55rem 0 .55rem .9rem; border-bottom:1px solid var(--rule-2);
        text-decoration:none; font-size:.94rem; color:var(--ink-2);
        border-left:3px solid transparent;
      }
      .rail nav a:hover{color:var(--ink);background:var(--sheet)}
      .rail nav a.on{color:var(--ink);border-left-color:var(--live);font-weight:600}
      .block{margin:auto 0 0;font-size:.78rem;border-top:1px solid var(--rule);padding-top:.9rem}
      .block div{display:flex;justify-content:space-between;gap:1rem;padding:.15rem 0}
      .block dt{color:var(--ink-3)}
      .block dd{margin:0;font-family:var(--mono);color:var(--ink)}

      /* ---- sheet ---- */
      main{padding:3.5rem 3rem 2rem;max-width:62rem}
      .head{border-bottom:2px solid var(--ink);padding-bottom:1.1rem;margin-bottom:2.25rem}
      h1{margin:0;font-size:2.1rem;line-height:1.15;letter-spacing:-.02em;font-weight:650}
      .lede{margin:.6rem 0 0;max-width:60ch;color:var(--ink-2)}
      h2{font-size:1.15rem;margin:2.5rem 0 .9rem;font-weight:650;letter-spacing:-.01em}
      h2:first-child{margin-top:0}
      p{max-width:68ch}

      /* ---- hero status ---- */
      .status{border:1px solid var(--rule);background:var(--white);padding:1.75rem 1.9rem}
      .status .now{display:flex;align-items:center;gap:.6rem;font-size:.85rem;color:var(--live);font-weight:600}
      .dot{width:.55rem;height:.55rem;border-radius:50%;background:var(--live);animation:beat 2.4s ease-in-out infinite}
      @keyframes beat{0%,100%{opacity:1}50%{opacity:.25}}
      @media (prefers-reduced-motion:reduce){.dot{animation:none}}
      .release{
        font-family:var(--mono); font-size:clamp(2rem,6vw,3.4rem);
        letter-spacing:-.04em; line-height:1; margin:.7rem 0 .35rem; word-break:break-all;
      }
      .status .note{margin:0;color:var(--ink-3);font-size:.9rem}

      /* ---- facts ---- */
      .facts{display:grid;grid-template-columns:repeat(auto-fit,minmax(11rem,1fr));
             border-top:1px solid var(--rule);margin-top:2rem}
      .facts div{padding:1rem 1.2rem 1.1rem;border-bottom:1px solid var(--rule-2);border-right:1px solid var(--rule-2)}
      .facts dt{font-size:.78rem;color:var(--ink-3)}
      .facts dd{margin:.3rem 0 0;font-family:var(--mono);font-size:1.05rem}

      /* ---- tables ---- */
      table{width:100%;border-collapse:collapse;font-size:.92rem;background:var(--white)}
      caption{text-align:left;color:var(--ink-3);font-size:.85rem;padding-bottom:.5rem}
      th,td{text-align:left;padding:.65rem .8rem;border-bottom:1px solid var(--rule-2);vertical-align:top}
      thead th{border-bottom:1px solid var(--rule);font-weight:600;color:var(--ink-2);font-size:.84rem}
      td code,.m{font-family:var(--mono);font-size:.88em}
      .scope{font-family:var(--mono);font-size:.78rem;color:var(--ink-2);white-space:nowrap}

      /* ---- pipeline sequence ---- */
      .seq{list-style:none;margin:0;padding:0;counter-reset:s;border-top:1px solid var(--rule)}
      .seq li{counter-increment:s;display:grid;grid-template-columns:2.6rem 1fr;gap:1.1rem;
              padding:1rem 0;border-bottom:1px solid var(--rule-2)}
      .seq li::before{content:counter(s,decimal-leading-zero);font-family:var(--mono);
              color:var(--ink-3);font-size:.95rem;padding-top:.1rem}
      .seq h3{margin:0;font-size:1rem;font-weight:650}
      .seq p{margin:.2rem 0 .35rem;color:var(--ink-2);font-size:.93rem}
      .seq code{background:var(--white);border:1px solid var(--rule-2);padding:.1rem .4rem;font-size:.83rem}

      /* ---- endpoints ---- */
      .ends{display:grid;gap:0;border-top:1px solid var(--rule)}
      .ends article{padding:1.1rem 0;border-bottom:1px solid var(--rule-2)}
      .verb{font-family:var(--mono);font-size:.78rem;color:var(--live);font-weight:700;margin-right:.6rem}
      .path{font-family:var(--mono);font-size:1rem}
      .ends p{margin:.35rem 0 0;color:var(--ink-2);font-size:.93rem}
      .try{display:inline-block;margin-top:.5rem;font-size:.85rem;color:var(--live);text-decoration:none;
           border-bottom:1px solid var(--live);padding-bottom:1px}

      .foot{margin-top:3.5rem;padding-top:1rem;border-top:1px solid var(--rule);
            display:flex;justify-content:space-between;gap:1rem;flex-wrap:wrap;
            font-size:.8rem;color:var(--ink-3);font-family:var(--mono)}

      @media (max-width:860px){
        body{grid-template-columns:1fr}
        .rail{position:static;height:auto;border-right:0;border-bottom:1px solid var(--rule)}
        .block{margin-top:1rem}
        main{padding:2rem 1.25rem}
      }
      """;
}
