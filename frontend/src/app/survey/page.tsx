"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";
import { movieService } from "@/services/movie.service";
import { surveyService } from "@/services/survey.service";
import { useToast } from "@/components/ui/toast";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { cn } from "@/lib/utils";
import {
  ArrowLeft,
  ArrowRight,
  CheckCircle2,
  Clapperboard,
  Heart,
  Loader2,
  Plus,
  Sparkles,
  ThumbsDown,
  Users,
  Wand2,
  X,
} from "lucide-react";
import type { Genre, SurveyRequest, UserSurveyDto } from "@/types";

interface Step {
  key: "genres" | "actors" | "directors" | "dislikes";
  title: string;
  description: string;
  icon: typeof Heart;
}

const STEPS: Step[] = [
  {
    key: "genres",
    title: "Pick your genres",
    description: "Which genres do you keep coming back to?",
    icon: Clapperboard,
  },
  {
    key: "actors",
    title: "Favorite actors",
    description: "Add a few performers whose movies you'll always watch.",
    icon: Users,
  },
  {
    key: "directors",
    title: "Directors you trust",
    description: "Filmmakers whose vision draws you in.",
    icon: Wand2,
  },
  {
    key: "dislikes",
    title: "Anything to avoid?",
    description: "Tropes, themes, or styles you want the AI to steer clear of.",
    icon: ThumbsDown,
  },
];

export default function SurveyPage() {
  const router = useRouter();
  const toast = useToast();
  const { user, isAuthenticated, isLoading: authLoading } = useAuth();

  const [stepIdx, setStepIdx] = useState(0);
  const [genres, setGenres] = useState<Genre[]>([]);
  const [favoriteGenres, setFavoriteGenres] = useState<string[]>([]);
  const [favoriteActors, setFavoriteActors] = useState<string[]>([]);
  const [favoriteDirectors, setFavoriteDirectors] = useState<string[]>([]);
  const [dislikes, setDislikes] = useState("");
  const [actorInput, setActorInput] = useState("");
  const [directorInput, setDirectorInput] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [existing, setExisting] = useState<UserSurveyDto | null>(null);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push("/login?next=/survey");
    }
  }, [authLoading, isAuthenticated, router]);

  const loadInitial = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    try {
      const [genreList, surveyResp] = await Promise.all([
        movieService.getAllGenres().catch(() => [] as Genre[]),
        surveyService.getSurvey(user.id).catch(() => null),
      ]);
      setGenres(genreList);
      if (surveyResp?.exists && surveyResp.survey) {
        const s = surveyResp.survey;
        setExisting(s);
        setFavoriteGenres(s.favoriteGenres ?? []);
        setFavoriteActors(s.favoriteActors ?? []);
        setFavoriteDirectors(s.favoriteDirectors ?? []);
        setDislikes(s.dislikes ?? "");
      }
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    loadInitial();
  }, [loadInitial]);

  const step = STEPS[stepIdx];

  const toggleGenre = (name: string) => {
    setFavoriteGenres((prev) =>
      prev.includes(name) ? prev.filter((g) => g !== name) : [...prev, name]
    );
  };

  const addChip = (
    value: string,
    list: string[],
    setter: (next: string[]) => void,
    clear: () => void
  ) => {
    const v = value.trim();
    if (!v) return;
    if (list.some((x) => x.toLowerCase() === v.toLowerCase())) {
      clear();
      return;
    }
    setter([...list, v]);
    clear();
  };

  const removeChip = (value: string, list: string[], setter: (next: string[]) => void) => {
    setter(list.filter((x) => x !== value));
  };

  const completion = useMemo(() => {
    let filled = 0;
    if (favoriteGenres.length > 0) filled++;
    if (favoriteActors.length > 0) filled++;
    if (favoriteDirectors.length > 0) filled++;
    if (dislikes.trim().length > 0) filled++;
    return Math.round((filled / STEPS.length) * 100);
  }, [favoriteGenres, favoriteActors, favoriteDirectors, dislikes]);

  const submit = async () => {
    if (!user) return;
    setSubmitting(true);
    try {
      const payload: SurveyRequest = {
        favoriteGenres,
        favoriteActors,
        favoriteDirectors,
        dislikes: dislikes.trim() || undefined,
      };
      await surveyService.submitSurvey(user.id, payload);
      if (typeof window !== "undefined") {
        // Survey is now complete — the /movies prompt should never re-appear.
        window.localStorage.removeItem("survey-prompt-dismissed");
      }
      toast.success(
        existing ? "Profile updated" : "Profile saved",
        "Head to recommendations to see fresh AI picks."
      );
      router.push("/recommendations");
    } catch (err) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        "Couldn't save your survey. Try again.";
      toast.error("Save failed", message);
    } finally {
      setSubmitting(false);
    }
  };

  if (authLoading || (!user && isAuthenticated)) {
    return <Skeleton className="h-screen w-full" />;
  }
  if (!user) return null;

  return (
    <div className="min-h-screen">
      {/* Header */}
      <div className="bg-gradient-to-b from-primary/15 to-transparent">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
          <Link
            href="/profile"
            className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground mb-4 text-sm transition-colors"
          >
            <ArrowLeft className="h-4 w-4" />
            Back to profile
          </Link>

          <div className="flex items-start gap-4">
            <div className="rounded-xl bg-primary/15 p-3">
              <Heart className="h-7 w-7 text-primary" />
            </div>
            <div className="flex-1">
              <h1 className="text-3xl sm:text-4xl font-bold text-foreground">
                {existing ? "Update your taste profile" : "Build your taste profile"}
              </h1>
              <p className="text-muted-foreground mt-1">
                Better signals → smarter recommendations. Skip any step that doesn't apply.
              </p>
            </div>
          </div>

          {/* Step indicator */}
          <div className="mt-8">
            <div className="flex items-center gap-2 mb-3">
              {STEPS.map((s, i) => {
                const StepIcon = s.icon;
                const active = i === stepIdx;
                const done = i < stepIdx;
                return (
                  <button
                    key={s.key}
                    type="button"
                    onClick={() => setStepIdx(i)}
                    className={cn(
                      "flex-1 flex items-center gap-2 rounded-lg border px-3 py-2 text-left transition-colors",
                      active
                        ? "border-primary bg-primary/15 text-foreground"
                        : done
                        ? "border-accent/40 bg-accent/10 text-foreground"
                        : "border-border bg-card text-muted-foreground hover:text-foreground"
                    )}
                  >
                    {done ? (
                      <CheckCircle2 className="h-4 w-4 text-accent flex-shrink-0" />
                    ) : (
                      <StepIcon className="h-4 w-4 flex-shrink-0" />
                    )}
                    <span className="hidden sm:inline text-xs font-medium truncate">
                      {s.title}
                    </span>
                  </button>
                );
              })}
            </div>
            <div className="h-1.5 rounded-full bg-secondary overflow-hidden">
              <div
                className="h-full bg-gradient-to-r from-primary to-accent transition-all duration-500 ease-out"
                style={{ width: `${completion}%` }}
              />
            </div>
            <p className="text-xs text-muted-foreground mt-2">
              {completion}% complete
            </p>
          </div>
        </div>
      </div>

      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 pb-12">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <step.icon className="h-5 w-5 text-primary" />
              {step.title}
            </CardTitle>
            <CardDescription>{step.description}</CardDescription>
          </CardHeader>
          <CardContent>
            {loading ? (
              <div className="space-y-2">
                {Array.from({ length: 6 }).map((_, i) => (
                  <Skeleton key={i} className="h-10" />
                ))}
              </div>
            ) : step.key === "genres" ? (
              <div className="flex flex-wrap gap-2">
                {genres.length === 0 && (
                  <p className="text-sm text-muted-foreground">
                    No genres loaded — the API may not have any yet. You can still add actors and
                    directors.
                  </p>
                )}
                {genres.map((g) => {
                  const active = favoriteGenres.includes(g.name);
                  return (
                    <button
                      key={g.id}
                      type="button"
                      onClick={() => toggleGenre(g.name)}
                      className={cn(
                        "inline-flex items-center gap-1.5 rounded-full border px-3.5 py-1.5 text-sm transition-all",
                        active
                          ? "border-primary bg-primary text-primary-foreground scale-105"
                          : "border-border bg-secondary/60 text-foreground hover:border-primary/40"
                      )}
                    >
                      {active && <CheckCircle2 className="h-3.5 w-3.5" />}
                      {g.name}
                    </button>
                  );
                })}
              </div>
            ) : step.key === "actors" ? (
              <ChipInput
                placeholder="e.g. Tom Hanks, Florence Pugh…"
                value={actorInput}
                onChange={setActorInput}
                onAdd={() =>
                  addChip(actorInput, favoriteActors, setFavoriteActors, () => setActorInput(""))
                }
                chips={favoriteActors}
                onRemove={(c) => removeChip(c, favoriteActors, setFavoriteActors)}
              />
            ) : step.key === "directors" ? (
              <ChipInput
                placeholder="e.g. Denis Villeneuve, Greta Gerwig…"
                value={directorInput}
                onChange={setDirectorInput}
                onAdd={() =>
                  addChip(
                    directorInput,
                    favoriteDirectors,
                    setFavoriteDirectors,
                    () => setDirectorInput("")
                  )
                }
                chips={favoriteDirectors}
                onRemove={(c) => removeChip(c, favoriteDirectors, setFavoriteDirectors)}
              />
            ) : (
              <div className="space-y-2">
                <textarea
                  value={dislikes}
                  onChange={(e) => setDislikes(e.target.value)}
                  placeholder="Excessive gore, slow burns, found-footage horror…"
                  rows={5}
                  className="flex w-full rounded-md border border-border bg-input px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                />
                <p className="text-xs text-muted-foreground">
                  Free-form — the AI will parse this naturally.
                </p>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Footer controls */}
        <div className="mt-6 flex items-center justify-between">
          <Button
            variant="ghost"
            onClick={() => setStepIdx((s) => Math.max(0, s - 1))}
            disabled={stepIdx === 0}
            className="gap-2"
          >
            <ArrowLeft className="h-4 w-4" />
            Back
          </Button>

          <div className="flex items-center gap-2">
            <span className="text-sm text-muted-foreground hidden sm:inline">
              Step {stepIdx + 1} of {STEPS.length}
            </span>

            {stepIdx < STEPS.length - 1 ? (
              <Button onClick={() => setStepIdx((s) => Math.min(STEPS.length - 1, s + 1))} className="gap-2">
                Next
                <ArrowRight className="h-4 w-4" />
              </Button>
            ) : (
              <Button onClick={submit} disabled={submitting} className="gap-2">
                {submitting ? (
                  <>
                    <Loader2 className="h-4 w-4 animate-spin" />
                    Saving…
                  </>
                ) : (
                  <>
                    <Sparkles className="h-4 w-4" />
                    {existing ? "Save changes" : "Save & generate picks"}
                  </>
                )}
              </Button>
            )}
          </div>
        </div>

        {/* Summary preview */}
        <Card className="mt-8 bg-card/60">
          <CardHeader>
            <CardTitle className="text-base">Your profile so far</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3 text-sm">
            <SummaryRow label="Genres" items={favoriteGenres} />
            <SummaryRow label="Actors" items={favoriteActors} />
            <SummaryRow label="Directors" items={favoriteDirectors} />
            <SummaryRow
              label="Avoid"
              items={dislikes.trim() ? [dislikes.trim()] : []}
              variant="text"
            />
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

function ChipInput({
  placeholder,
  value,
  onChange,
  onAdd,
  chips,
  onRemove,
}: {
  placeholder: string;
  value: string;
  onChange: (next: string) => void;
  onAdd: () => void;
  chips: string[];
  onRemove: (chip: string) => void;
}) {
  return (
    <div>
      <div className="flex gap-2">
        <Input
          value={value}
          onChange={(e) => onChange(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              e.preventDefault();
              onAdd();
            }
          }}
          placeholder={placeholder}
        />
        <Button type="button" onClick={onAdd} className="gap-1 flex-shrink-0">
          <Plus className="h-4 w-4" />
          Add
        </Button>
      </div>

      {chips.length > 0 && (
        <div className="mt-4 flex flex-wrap gap-2">
          {chips.map((c) => (
            <span
              key={c}
              className="inline-flex items-center gap-1.5 rounded-full bg-primary/15 px-3 py-1 text-sm font-medium text-primary border border-primary/30"
            >
              {c}
              <button
                type="button"
                className="text-primary/70 hover:text-primary"
                onClick={() => onRemove(c)}
                aria-label={`Remove ${c}`}
              >
                <X className="h-3.5 w-3.5" />
              </button>
            </span>
          ))}
        </div>
      )}
    </div>
  );
}

function SummaryRow({
  label,
  items,
  variant = "chips",
}: {
  label: string;
  items: string[];
  variant?: "chips" | "text";
}) {
  return (
    <div>
      <p className="text-xs uppercase tracking-wider text-muted-foreground mb-1">{label}</p>
      {items.length === 0 ? (
        <p className="text-muted-foreground/70 text-sm italic">Not set</p>
      ) : variant === "text" ? (
        <p className="text-foreground">{items[0]}</p>
      ) : (
        <div className="flex flex-wrap gap-1.5">
          {items.map((i) => (
            <Badge key={i} variant="secondary" className="text-xs">
              {i}
            </Badge>
          ))}
        </div>
      )}
    </div>
  );
}
