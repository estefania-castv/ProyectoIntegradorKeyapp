import { useEffect, useState } from "react";

// Devuelve la hora actual y se actualiza sola (para que botones y estados cambien cuando pasa el tiempo).
export function useAhora(intervaloMs = 30000) {
  const [ahora, setAhora] = useState(() => new Date());
  useEffect(() => {
    const id = setInterval(() => setAhora(new Date()), intervaloMs);
    return () => clearInterval(id);
  }, [intervaloMs]);
  return ahora;
}
