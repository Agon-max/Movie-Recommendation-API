"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";
import { Button } from "@/components/ui/button";
import { Dialog } from "@/components/ui/dialog";
import { Gift, Sparkles, Trophy } from "lucide-react";

/**
 * Mounted globally. When the user's first login awards bonus points,
 * AuthContext sets `welcomeBonus`; we celebrate it once and clear it.
 */
export function WelcomeBonusModal() {
  const router = useRouter();
  const { welcomeBonus, clearWelcomeBonus, user } = useAuth();
  const [animateIn, setAnimateIn] = useState(false);

  useEffect(() => {
    if (welcomeBonus !== null) {
      const r = requestAnimationFrame(() => setAnimateIn(true));
      return () => cancelAnimationFrame(r);
    }
    setAnimateIn(false);
  }, [welcomeBonus]);

  if (welcomeBonus === null) return null;

  const handleClose = () => {
    setAnimateIn(false);
    setTimeout(clearWelcomeBonus, 200);
  };

  const handleSurvey = () => {
    handleClose();
    router.push("/survey");
  };

  return (
    <Dialog open={true} onClose={handleClose} className="max-w-md overflow-hidden">
      <div className="-mx-6 -mt-6 mb-4 px-6 pt-8 pb-6 bg-gradient-to-br from-accent/30 via-primary/15 to-transparent text-center relative">
        <div className="pointer-events-none absolute inset-0 overflow-hidden">
          {Array.from({ length: 12 }).map((_, i) => (
            <span
              key={i}
              className="absolute h-1.5 w-1.5 rounded-full bg-accent animate-pulse"
              style={{
                top: `${(i * 13) % 100}%`,
                left: `${(i * 27) % 100}%`,
                animationDelay: `${i * 0.1}s`,
                opacity: 0.6,
              }}
            />
          ))}
        </div>

        <div
          className={`relative mx-auto h-20 w-20 rounded-full bg-accent/20 flex items-center justify-center mb-3 transition-all duration-500 ${
            animateIn ? "scale-100 rotate-0" : "scale-0 -rotate-180"
          }`}
        >
          <Trophy className="h-10 w-10 text-accent" />
          <Sparkles className="absolute -top-1 -right-1 h-5 w-5 text-primary animate-pulse" />
        </div>

        <p className="text-xs uppercase tracking-widest text-accent font-semibold mb-1">
          Welcome aboard
        </p>
        <h2 className="text-2xl font-bold text-foreground">
          {user?.username ? `Hey ${user.username},` : "Welcome!"}
        </h2>
        <p className="mt-1 text-sm text-muted-foreground">
          Here's a little something to get you started.
        </p>
      </div>

      <div className="text-center">
        <div className="inline-flex items-baseline gap-2 mb-4">
          <span
            className={`text-5xl font-bold bg-gradient-to-r from-accent to-primary bg-clip-text text-transparent transition-all duration-700 ${
              animateIn ? "translate-y-0 opacity-100" : "translate-y-3 opacity-0"
            }`}
          >
            +{welcomeBonus}
          </span>
          <span className="text-lg font-semibold text-muted-foreground">points</span>
        </div>

        <p className="text-sm text-muted-foreground max-w-xs mx-auto mb-6">
          Spend them on gift cards, cash payouts, and discount codes. Earn more by
          watching movies, writing reviews, and taking the taste survey.
        </p>

        <div className="flex flex-col sm:flex-row gap-2 justify-center">
          <Button onClick={handleSurvey} className="gap-2">
            <Sparkles className="h-4 w-4" />
            Personalize my picks
          </Button>
          <Button variant="outline" onClick={handleClose} className="gap-2">
            <Gift className="h-4 w-4" />
            Start exploring
          </Button>
        </div>
      </div>
    </Dialog>
  );
}
