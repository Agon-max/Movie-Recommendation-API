"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";
import { movieService } from "@/services/movie.service";
import { surveyService } from "@/services/survey.service";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { MovieCard } from "@/components/movies/movie-card";
import { useToast } from "@/components/ui/toast";
import {
  ArrowLeft,
  Brain,
  Loader2,
  RefreshCw,
  Sparkles,
  Stars,
  Wand2,
  Zap,
} from "lucide-react";
import type { Movie, SurveyResponse } from "@/types";

const COUNT_OPTIONS = [5, 10, 15, 25];

export default function RecommendationsPage() {
  const router = useRouter();
  const toast = useToast();
  const { user, isAuthenticated, isLoading: authLoading } = useAuth();

  const [movies, setMovies] = useState<Movie[]>([]);
  const [count, setCount] = useState(10);
  const [loading, setLoading] = useState(false);
  const [hasFetched, setHasFetched] = useState(false);
  const [survey, setSurvey] = useState<SurveyResponse | null>(null);
  const [requestStart, setRequestStart] = useState<number | null>(null);
  const [elapsed, setElapsed] = useState<number>(0);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push("/login?next=/recommendations");
    }
  }, [authLoading, isAuthenticated, router]);

  // Live ticker for the "thinking…" elapsed timer
  useEffect(() => {
    if (!loading || requestStart === null) return;
    const interval = window.setInterval(() => {
      setElapsed(Date.now() - requestStart);
    }, 100);
    return () => window.clearInterval(interval);
  }, [loading, requestStart]);

  const fetchSurvey = useCallback(async () => {
    if (!user) return;
    try {
      const data = await surveyService.getSurvey(user.id);
      setSurvey(data);
    } catch {
      setSurvey({ exists: false, survey: null, message: null });
    }
  }, [user]);

  const fetchRecs = useCallback(
    async (n: number) => {
      if (!user) return;
      setLoading(true);
      setRequestStart(Date.now());
      setElapsed(0);
      try {
        const data = await movieService.getRecommendations(user.id, n);
        setMovies(data);
        setHasFetched(true);
        if (data.length === 0) {
          toast.info(
            "No picks yet",
            "Try completing your taste profile to give the AI more to work with."
          );
        } else {
          toast.success(`Fresh picks loaded`, `${data.length} movies tailored just for you`);
        }
      } catch (err) {
        const message =
          (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
          "Couldn't reach the recommendation engine. Try again shortly.";
        toast.error("Recommendation failed", message);
      } finally {
        setLoading(false);
        setRequestStart(null);
      }
    },
    [user, toast]
  );

  useEffect(() => {
    if (user) {
      fetchSurvey();
    }
  }, [user, fetchSurvey]);

  const hasSurvey = survey?.exists === true;

  const headerStats = useMemo(
    () => [
      { label: "Picks loaded", value: movies.length || "—" },
      { label: "Target count", value: count },
      {
        label: "Profile",
        value: hasSurvey ? "Complete" : "Not set",
      },
    ],
    [movies.length, count, hasSurvey]
  );

  if (authLoading || (!user && isAuthenticated)) {
    return <Skeleton className="h-screen w-full" />;
  }
  if (!user) return null;

  return (
    <div className="min-h-screen">
      {/* Hero */}
      <div className="relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-primary/20 via-background to-accent/10" />
        <div className="absolute -top-32 -right-32 h-96 w-96 rounded-full bg-primary/20 blur-3xl" />
        <div className="absolute -bottom-32 -left-32 h-96 w-96 rounded-full bg-accent/20 blur-3xl" />

        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <Link
            href="/movies"
            className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground mb-6 text-sm transition-colors"
          >
            <ArrowLeft className="h-4 w-4" />
            Back to browse
          </Link>

          <div className="grid lg:grid-cols-[1fr_auto] gap-8 items-end">
            <div>
              <div className="inline-flex items-center gap-2 rounded-full border border-primary/30 bg-primary/10 px-3 py-1 text-xs font-medium text-primary mb-4">
                <Brain className="h-3.5 w-3.5" />
                Powered by OpenRouter AI
              </div>
              <h1 className="text-4xl sm:text-5xl font-bold text-foreground mb-3 leading-tight">
                Your next favorite,
                <br />
                <span className="bg-gradient-to-r from-primary via-accent to-primary bg-clip-text text-transparent">
                  curated by AI
                </span>
              </h1>
              <p className="text-muted-foreground text-lg max-w-2xl">
                Pulls from your watch history, ratings, reviews, and taste survey to suggest
                titles you're statistically going to love.
              </p>
            </div>

            <div className="hidden lg:flex gap-3">
              {headerStats.map((s) => (
                <div
                  key={s.label}
                  className="rounded-xl border border-border bg-card/70 backdrop-blur px-4 py-3 min-w-[100px] text-center"
                >
                  <p className="text-2xl font-bold text-foreground">{s.value}</p>
                  <p className="text-[10px] uppercase tracking-wider text-muted-foreground">
                    {s.label}
                  </p>
                </div>
              ))}
            </div>
          </div>

          {/* Controls */}
          <div className="mt-8 rounded-2xl border border-border bg-card/70 backdrop-blur p-5">
            <div className="flex flex-wrap items-center gap-4">
              <div className="flex items-center gap-2">
                <Stars className="h-4 w-4 text-primary" />
                <span className="text-sm font-medium text-foreground">How many picks?</span>
              </div>
              <div className="flex gap-2">
                {COUNT_OPTIONS.map((n) => (
                  <Badge
                    key={n}
                    variant={count === n ? "default" : "secondary"}
                    onClick={() => setCount(n)}
                    className="cursor-pointer select-none px-3 py-1 hover:scale-105 transition-transform"
                  >
                    {n}
                  </Badge>
                ))}
              </div>

              <div className="ml-auto flex flex-wrap items-center gap-2">
                {!hasSurvey && (
                  <Link href="/survey">
                    <Button variant="outline" size="sm" className="gap-2">
                      <Wand2 className="h-4 w-4" />
                      Complete profile
                    </Button>
                  </Link>
                )}
                <Button
                  onClick={() => fetchRecs(count)}
                  disabled={loading}
                  size="lg"
                  className="gap-2"
                >
                  {loading ? (
                    <>
                      <Loader2 className="h-4 w-4 animate-spin" />
                      Thinking{elapsed > 500 ? ` (${(elapsed / 1000).toFixed(1)}s)` : "…"}
                    </>
                  ) : hasFetched ? (
                    <>
                      <RefreshCw className="h-4 w-4" />
                      Regenerate
                    </>
                  ) : (
                    <>
                      <Sparkles className="h-4 w-4" />
                      Get my picks
                    </>
                  )}
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        {/* Body states */}
        {!hasFetched && !loading && (
          <EmptyState onStart={() => fetchRecs(count)} hasSurvey={hasSurvey} />
        )}

        {loading && <ThinkingState count={count} elapsed={elapsed} />}

        {hasFetched && !loading && movies.length > 0 && (
          <div>
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-2xl font-bold text-foreground flex items-center gap-2">
                  <Zap className="h-6 w-6 text-accent" />
                  Tonight's picks
                </h2>
                <p className="text-sm text-muted-foreground mt-1">
                  Ranked by AI confidence — top spots are the strongest matches.
                </p>
              </div>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => fetchRecs(count)}
                className="gap-2"
              >
                <RefreshCw className="h-3.5 w-3.5" />
                Shuffle
              </Button>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4 sm:gap-6">
              {movies.map((movie, idx) => (
                <div key={movie.tmdbId ?? movie.id ?? idx} className="relative">
                  {idx < 3 && (
                    <div className="absolute -top-2 -left-2 z-10 flex items-center gap-1 rounded-full border border-accent/40 bg-background/90 backdrop-blur px-2.5 py-1 text-[10px] font-bold uppercase tracking-wider text-accent shadow-lg">
                      <Sparkles className="h-3 w-3" />
                      Top match #{idx + 1}
                    </div>
                  )}
                  <MovieCard movie={movie} />
                </div>
              ))}
            </div>
          </div>
        )}

        {hasFetched && !loading && movies.length === 0 && (
          <Card>
            <CardContent className="py-16 text-center">
              <Brain className="h-12 w-12 text-muted-foreground mx-auto mb-4 opacity-50" />
              <h3 className="font-semibold text-foreground mb-1">
                The AI needs more to work with
              </h3>
              <p className="text-sm text-muted-foreground mb-6 max-w-md mx-auto">
                We couldn't generate strong recommendations yet. Try completing your taste
                survey or watching a few more movies first.
              </p>
              <div className="flex flex-wrap items-center justify-center gap-3">
                <Link href="/survey">
                  <Button variant="default" className="gap-2">
                    <Wand2 className="h-4 w-4" />
                    Take the taste survey
                  </Button>
                </Link>
                <Link href="/movies">
                  <Button variant="outline" className="gap-2">
                    Browse movies
                  </Button>
                </Link>
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}

function EmptyState({ onStart, hasSurvey }: { onStart: () => void; hasSurvey: boolean }) {
  return (
    <div className="grid lg:grid-cols-3 gap-6">
      <Card className="lg:col-span-2 overflow-hidden">
        <div className="bg-gradient-to-br from-primary/10 via-card to-card p-8">
          <div className="flex items-center gap-3 mb-3">
            <div className="p-2 rounded-lg bg-primary/15">
              <Sparkles className="h-5 w-5 text-primary" />
            </div>
            <h3 className="text-xl font-semibold text-foreground">Ready when you are</h3>
          </div>
          <p className="text-muted-foreground mb-6 max-w-xl">
            Hit "Get my picks" above and our AI will scan your activity to surface movies
            you're most likely to enjoy. Re-roll any time.
          </p>
          <Button onClick={onStart} className="gap-2">
            <Sparkles className="h-4 w-4" />
            Generate picks now
          </Button>
        </div>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">What we use</CardTitle>
          <CardDescription>Signals fed into the AI</CardDescription>
        </CardHeader>
        <CardContent className="space-y-2.5 text-sm">
          <Signal label="Watch history" hint="Movies you've marked watched" />
          <Signal label="Ratings & reviews" hint="What you scored & wrote about" />
          <Signal
            label="Taste survey"
            hint={hasSurvey ? "✓ Profile complete" : "Not yet set — improve picks"}
            highlight={!hasSurvey}
          />
          <Signal label="Genre patterns" hint="Implicit preferences over time" />
        </CardContent>
      </Card>
    </div>
  );
}

function Signal({
  label,
  hint,
  highlight,
}: {
  label: string;
  hint: string;
  highlight?: boolean;
}) {
  return (
    <div
      className={`flex items-start justify-between rounded-lg border px-3 py-2 ${
        highlight ? "border-primary/40 bg-primary/10" : "border-border bg-secondary/40"
      }`}
    >
      <div>
        <p className="font-medium text-foreground">{label}</p>
        <p className="text-xs text-muted-foreground">{hint}</p>
      </div>
    </div>
  );
}

function ThinkingState({ count, elapsed }: { count: number; elapsed: number }) {
  const phase =
    elapsed < 1500
      ? "Reading your watch history"
      : elapsed < 3000
      ? "Analyzing genre preferences"
      : elapsed < 5000
      ? "Scoring candidate titles"
      : "Ranking the strongest matches";

  return (
    <div>
      <Card className="mb-6 overflow-hidden border-primary/40">
        <CardContent className="py-8">
          <div className="flex items-center gap-4">
            <div className="relative flex h-12 w-12 items-center justify-center">
              <span className="absolute inset-0 rounded-full bg-primary/30 animate-ping" />
              <span className="relative inline-flex h-10 w-10 items-center justify-center rounded-full bg-primary/20">
                <Brain className="h-5 w-5 text-primary animate-pulse" />
              </span>
            </div>
            <div className="flex-1">
              <p className="text-foreground font-semibold">{phase}…</p>
              <p className="text-xs text-muted-foreground">
                Targeting {count} picks • {(elapsed / 1000).toFixed(1)}s elapsed
              </p>
            </div>
          </div>
        </CardContent>
      </Card>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4 sm:gap-6">
        {Array.from({ length: Math.min(count, 10) }).map((_, i) => (
          <div key={i} className="relative">
            <Skeleton className="aspect-[2/3] rounded-lg" />
            <Skeleton className="h-4 w-3/4 mt-2" />
            <Skeleton className="h-3 w-1/2 mt-1" />
          </div>
        ))}
      </div>
    </div>
  );
}
