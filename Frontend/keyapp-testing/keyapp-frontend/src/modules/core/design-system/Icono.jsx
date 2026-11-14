import "./iconos.css";

// Iconos SVG propios (sin dependencias). Cada parte móvil tiene su clase para animarla desde iconos.css.
const DIBUJOS = {
  key: (
    <g className="ic-key-g">
      <circle cx="7.5" cy="15.5" r="4.5" />
      <path d="M10.7 12.3 20 3" />
      <path d="M16 7l3 3" />
      <path d="M13.5 9.5l2 2" />
    </g>
  ),
  lock: (
    <>
      <rect x="4.5" y="11" width="15" height="10" rx="2.5" />
      <path className="ic-shackle" d="M8 11V7.5a4 4 0 0 1 8 0V11" />
      <circle className="ic-keyhole" cx="12" cy="16" r="1.3" fill="currentColor" stroke="none" />
    </>
  ),
  door: (
    <>
      <path d="M5 21V4.5A1.5 1.5 0 0 1 6.5 3h11A1.5 1.5 0 0 1 19 4.5V21" />
      <path d="M3 21h18" />
      <rect className="ic-door-inside" x="7.5" y="5.5" width="9" height="15.5" rx=".8" fill="currentColor" stroke="none" />
      <g className="ic-door-panel">
        <rect x="7.5" y="5.5" width="9" height="15.5" rx=".8" />
        <circle cx="14" cy="13.5" r=".9" fill="currentColor" stroke="none" />
      </g>
    </>
  ),
  calendar: (
    <>
      <rect x="3.5" y="5" width="17" height="15.5" rx="2.5" />
      <path d="M3.5 10h17M8 3v4M16 3v4" />
      <circle className="ic-dot" cx="12" cy="15" r="1.5" fill="currentColor" stroke="none" />
    </>
  ),
  dashboard: (
    <>
      <path className="ic-bar b1" d="M6 20v-7" />
      <path className="ic-bar b2" d="M12 20V5" />
      <path className="ic-bar b3" d="M18 20v-10" />
    </>
  ),
  plus: <path className="ic-plus" d="M12 5v14M5 12h14" />,
  users: (
    <>
      <circle cx="9" cy="8" r="3.5" />
      <path d="M2.5 20c0-3.6 2.9-6 6.5-6s6.5 2.4 6.5 6" />
      <circle className="ic-user2" cx="17.5" cy="9" r="2.6" />
      <path className="ic-user2" d="M17 14.2c2.8 0 4.5 1.8 4.5 4.3" />
    </>
  ),
  user: (
    <>
      <circle cx="12" cy="8" r="3.8" />
      <path d="M4.5 20.5c0-4 3.4-6.5 7.5-6.5s7.5 2.5 7.5 6.5" />
    </>
  ),
  aula: (
    <>
      <rect x="3" y="4" width="18" height="12" rx="2" />
      <path className="ic-line l1" d="M7 8.5h7" />
      <path className="ic-line l2" d="M7 12h4.5" />
      <path d="M8 21l4-5 4 5" />
    </>
  ),
  logout: (
    <>
      <path d="M10 4H6.5A2.5 2.5 0 0 0 4 6.5v11A2.5 2.5 0 0 0 6.5 20H10" />
      <g className="ic-arrow">
        <path d="M14 8l4 4-4 4M18 12H9" />
      </g>
    </>
  ),
  search: (
    <g className="ic-search-g">
      <circle cx="11" cy="11" r="6.5" />
      <path d="M16 16l4.5 4.5" />
    </g>
  ),
  swap: (
    <>
      <g className="ic-a1">
        <path d="M4 8h15M15.5 4.5 19 8l-3.5 3.5" />
      </g>
      <g className="ic-a2">
        <path d="M20 16H5M8.5 12.5 5 16l3.5 3.5" />
      </g>
    </>
  ),
  x: <path className="ic-x" d="M6 6l12 12M18 6 6 18" />,
  check: <path className="ic-check" d="M5 12.5l4.5 4.5L19 7.5" />,
  clock: (
    <>
      <circle cx="12" cy="12" r="8.5" />
      <path className="ic-hand" d="M12 7.5V12l3 2" />
    </>
  ),
  alerta: (
    <>
      <path d="M12 4 21 19.5H3z" />
      <path d="M12 10v4.5" />
      <circle cx="12" cy="17" r=".6" fill="currentColor" />
    </>
  ),
  mail: (
    <>
      <rect x="3" y="5.5" width="18" height="13" rx="2.5" />
      <path className="ic-flap" d="M4 7.5l8 6 8-6" />
    </>
  ),
  signal: (
    <>
      <path className="ic-wave w2" d="M4.5 10.5a10.5 10.5 0 0 1 15 0" />
      <path className="ic-wave w1" d="M8 14a6 6 0 0 1 8 0" />
      <circle cx="12" cy="18" r="1.2" fill="currentColor" stroke="none" />
    </>
  ),
  chat: (
    <>
      <path d="M4 6a2.5 2.5 0 0 1 2.5-2.5h11A2.5 2.5 0 0 1 20 6v8a2.5 2.5 0 0 1-2.5 2.5H11L7 20.5v-4H6.5A2.5 2.5 0 0 1 4 14z" />
      <circle className="ic-d d1" cx="8.5" cy="10" r="1" fill="currentColor" stroke="none" />
      <circle className="ic-d d2" cx="12" cy="10" r="1" fill="currentColor" stroke="none" />
      <circle className="ic-d d3" cx="15.5" cy="10" r="1" fill="currentColor" stroke="none" />
    </>
  ),
  send: (
    <g className="ic-plane">
      <path d="M21 3 10.5 13.5" />
      <path d="M21 3l-6.5 18-4-7.5L3 9.5z" />
    </g>
  ),
  flag: (
    <>
      <path d="M5 21V4" />
      <path className="ic-flag" d="M5 4h11l-2 4 2 4H5" />
    </>
  ),
  bolt: <path className="ic-bolt" d="M13 2 4.5 13.5H11L10 22l8.5-11.5H12z" />,
  wrench: (
    <path
      className="ic-wrench"
      d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"
    />
  ),
  vacio: (
    <>
      <rect x="4" y="6" width="16" height="13" rx="2.5" />
      <path d="M4 11h16" />
      <path d="M9 15h6" />
    </>
  ),
};

/**
 * <Icono nombre="door" tamano={20} animar="hover" />
 * animar:
 *   "hover"  -> se mueve cuando el cursor pasa por el icono o por un padre con la clase .kp-anim
 *   "siempre" -> animación continua y suave
 *   "no"     -> estático
 */
export default function Icono({ nombre, tamano = 20, animar = "hover", className = "", titulo }) {
  const dibujo = DIBUJOS[nombre];
  if (!dibujo) return null;
  return (
    <svg
      className={`ic ic-${nombre} ic--${animar} ${className}`.trim()}
      width={tamano}
      height={tamano}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      role={titulo ? "img" : undefined}
      aria-hidden={titulo ? undefined : "true"}
      focusable="false"
    >
      {titulo && <title>{titulo}</title>}
      {dibujo}
    </svg>
  );
}
