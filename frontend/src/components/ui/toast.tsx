"use client";

import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { AlertCircle, CheckCircle2, Info, Trophy, X } from "lucide-react";
import { cn } from "@/lib/utils";

export type ToastTone = "success" | "error" | "info" | "points";

export interface Toast {
  id: number;
  tone: ToastTone;
  title: string;
  description?: string;
  points?: number;
  durationMs?: number;
}

interface ToastContextType {
  toast: (t: Omit<Toast, "id">) => void;
  success: (title: string, description?: string) => void;
  error: (title: string, description?: string) => void;
  info: (title: string, description?: string) => void;
  pointsEarned: (points: number, title?: string) => void;
}

const ToastContext = createContext<ToastContextType | undefined>(undefined);

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([]);

  const dismiss = useCallback((id: number) => {
    setToasts((curr) => curr.filter((t) => t.id !== id));
  }, []);

  const push = useCallback(
    (t: Omit<Toast, "id">) => {
      const id = Date.now() + Math.random();
      const next: Toast = { id, durationMs: 3500, ...t };
      setToasts((curr) => [...curr, next]);
      window.setTimeout(() => dismiss(id), next.durationMs);
    },
    [dismiss]
  );

  const value = useMemo<ToastContextType>(
    () => ({
      toast: push,
      success: (title, description) => push({ tone: "success", title, description }),
      error: (title, description) => push({ tone: "error", title, description }),
      info: (title, description) => push({ tone: "info", title, description }),
      pointsEarned: (points, title = "Points earned!") =>
        push({ tone: "points", title, points, durationMs: 4000 }),
    }),
    [push]
  );

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div
        aria-live="polite"
        className="fixed top-20 right-4 z-[100] flex flex-col gap-3 max-w-sm pointer-events-none"
      >
        {toasts.map((t) => (
          <ToastCard key={t.id} toast={t} onDismiss={() => dismiss(t.id)} />
        ))}
      </div>
    </ToastContext.Provider>
  );
}

function ToastCard({ toast, onDismiss }: { toast: Toast; onDismiss: () => void }) {
  const [enter, setEnter] = useState(false);
  useEffect(() => {
    const r = requestAnimationFrame(() => setEnter(true));
    return () => cancelAnimationFrame(r);
  }, []);

  const styles =
    toast.tone === "success"
      ? "border-green-500/30 bg-green-500/10 text-green-300"
      : toast.tone === "error"
      ? "border-destructive/30 bg-destructive/10 text-destructive"
      : toast.tone === "points"
      ? "border-accent/40 bg-gradient-to-br from-accent/20 to-primary/10 text-accent-foreground"
      : "border-primary/30 bg-primary/10 text-primary";

  const Icon =
    toast.tone === "success"
      ? CheckCircle2
      : toast.tone === "error"
      ? AlertCircle
      : toast.tone === "points"
      ? Trophy
      : Info;

  return (
    <div
      className={cn(
        "pointer-events-auto flex items-start gap-3 rounded-xl border px-4 py-3 shadow-2xl backdrop-blur transition-all duration-300",
        styles,
        enter ? "translate-x-0 opacity-100" : "translate-x-8 opacity-0"
      )}
      role="status"
    >
      <Icon className={cn("h-5 w-5 mt-0.5 flex-shrink-0", toast.tone === "points" && "text-accent")} />
      <div className="flex-1 min-w-0">
        <p className="font-semibold text-foreground leading-tight">
          {toast.title}
          {typeof toast.points === "number" && (
            <span className="ml-2 inline-flex items-center gap-1 rounded-full bg-accent/30 px-2 py-0.5 text-xs font-bold text-accent">
              +{toast.points} pts
            </span>
          )}
        </p>
        {toast.description && (
          <p className="text-sm text-muted-foreground mt-0.5">{toast.description}</p>
        )}
      </div>
      <button
        type="button"
        onClick={onDismiss}
        className="text-muted-foreground hover:text-foreground transition-colors"
        aria-label="Dismiss"
      >
        <X className="h-4 w-4" />
      </button>
    </div>
  );
}

export function useToast(): ToastContextType {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error("useToast must be used within a ToastProvider");
  return ctx;
}
