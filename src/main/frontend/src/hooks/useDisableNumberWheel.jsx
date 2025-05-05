import { useEffect } from "react";

export default function useDisableNumberWheel() {
    useEffect(() => {
        const handleWheel = (e) => {
            const el = document.activeElement;
            if (el && el.type === "number") {
                el.blur();
            }
        };

        window.addEventListener("wheel", handleWheel, { passive: true, capture: true });
        return () => window.removeEventListener("wheel", handleWheel, { capture: true });
    }, []);
}