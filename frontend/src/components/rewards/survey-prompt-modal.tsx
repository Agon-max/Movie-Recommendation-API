"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";
import { surveyService } from "@/services/survey.service";
import { Button } from "@/components/ui/button";
import { Dialog } from "@/components/ui/dialog";
import { Brain, Sparkles, Wand2, ArrowRight, X } from "lucide-react";

const DISMISS_KEY = "survey-prompt-dismissed";

/**
 * Reminds authenticated users without a completed taste survey to take it
 * before browsing. Dismissal is session-scoped (sessionStorage), so closing
 * it doesn't hide it forever — it returns on the next visit. The /survey
 * submit handler also clears any leftover localStorage flag from older
 * builds so the prompt re-surfaces correctly until completed.
 */
export function SurveyPromptModal() {
  const router = useRouter();
  const { user, isAuthenticated, isLoading } = useAuth();
  const [open, setOpen] = useState(false);

  useEffect(() => {
    if (isLoading || !isAuthenticated || !user) return;

    if (typeof window === "undefined") return;
    // Clean up any sticky localStorage flag from older builds so the modal
    // can re-appear (we now only honor session-scoped dismissals).
    window.localStorage.removeItem(DISMISS_KEY);
    if (window.sessionStorage.getItem(DISMISS_KEY) === "true") return;

    let cancelled = false;
    (async () => {
      try {
        const status = await surveyService.getSurvey(user.id);
        if (cancelled) return;
        if (!status.exists) {
          setOpen(true);
        }
      } catch {
        // If the survey endpoint fails, don't block browsing.
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [user, isAuthenticated, isLoading]);

  const dismiss = (persist: boolean) => {
    setOpen(false);
    if (persist && typeof window !== "undefined") {
      window.sessionStorage.setItem(DISMISS_KEY, "true");
    }
  };

  const takeSurvey = () => {
    setOpen(false);
    router.push("/survey");
  };

  if (!open) return null;

  return (
    <Dialog open={open} onClose={() => dismiss(true)} className="max-w-lg overflow-hidden">
      <div className="-mx-6 -mt-6 mb-5 px-6 pt-7 pb-5 bg-gradient-to-br from-primary/25 via-accent/10 to-transparent">
        <div className="flex items-center gap-3 mb-3">
          <div className="rounded-xl bg-primary/20 p-2.5 backdrop-blur">
            <Brain className="h-6 w-6 text-primary" />
          </div>
          <div>
            <p className="text-[10px] uppercase tracking-widest text-primary font-semibold">
              Smarter recommendations
            </p>
            <h2 className="text-xl font-bold text-foreground leading-tight">
              Make your picks personal
            </h2>
          </div>
        </div>
        <p className="text-sm text-muted-foreground">
          Spend 30 seconds telling us what you love — genres, actors, directors — and
          our AI will tailor every recommendation to your taste.
        </p>
      </div>

      <ul className="space-y-2.5 mb-6 text-sm">
        <BenefitRow text="AI uses your survey as a head-start, even before you watch anything" />
        <BenefitRow text="Skip the cold-start problem — get great picks immediately" />
        <BenefitRow text="Update or skip any time from your profile" />
      </ul>

      <div className="flex flex-col sm:flex-row-reverse gap-2">
        <Button onClick={takeSurvey} className="gap-2">
          <Wand2 className="h-4 w-4" />
          Take the survey
          <ArrowRight className="h-4 w-4" />
        </Button>
        <Button
          variant="ghost"
          onClick={() => dismiss(true)}
          className="gap-2 text-muted-foreground"
        >
          <X className="h-4 w-4" />
          Just let me browse
        </Button>
      </div>

      <p className="text-[11px] text-muted-foreground text-center mt-4">
        You can always take it later from <span className="text-foreground">Profile → Build profile</span>.
      </p>
    </Dialog>
  );
}

function BenefitRow({ text }: { text: string }) {
  return (
    <li className="flex items-start gap-2.5 rounded-lg border border-border bg-secondary/40 px-3 py-2">
      <Sparkles className="h-4 w-4 text-accent flex-shrink-0 mt-0.5" />
      <span className="text-foreground/90">{text}</span>
    </li>
  );
}
