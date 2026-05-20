"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";
import { userService } from "@/services/user.service";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
  ArrowLeft,
  Calendar,
  Clock,
  Film,
  Flame,
  History,
  Sparkles,
  Trophy,
} from "lucide-react";
import { formatPoints } from "@/lib/utils";
import type { PointHistory } from "@/types";

export default function WatchHistoryPage() {
  const router = useRouter();
  const { user, isAuthenticated, isLoading: authLoading } = useAuth();
  const [history, setHistory] = useState<PointHistory[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push("/login?next=/watch-history");
    }
  }, [authLoading, isAuthenticated, router]);

  useEffect(() => {
    if (!user) return;
    let cancelled = false;
    (async () => {
      setLoading(true);
      try {
        const data = await userService.getPointHistory(user.id);
        if (!cancelled) setHistory(data);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [user]);

  const watchEvents = useMemo(
    () => history.filter((h) => h.eventType === "WATCH_MOVIE"),
    [history]
  );

  const reviewEvents = useMemo(
    () => history.filter((h) => h.eventType === "WRITE_REVIEW"),
    [history]
  );

  // Streak calc — consecutive days with WATCH_MOVIE event
  const streak = useMemo(() => {
    if (watchEvents.length === 0) return 0;
    const days = new Set<string>(
      watchEvents.map((e) => new Date(e.createdAt).toDateString())
    );
    let count = 0;
    const cursor = new Date();
    while (days.has(cursor.toDateString())) {
      count += 1;
      cursor.setDate(cursor.getDate() - 1);
    }
    return count;
  }, [watchEvents]);

  // Group watch events by day for the activity heatmap-ish strip
  const past14Days = useMemo(() => {
    const days: { date: Date; key: string; count: number }[] = [];
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    for (let i = 13; i >= 0; i--) {
      const d = new Date(today);
      d.setDate(today.getDate() - i);
      days.push({ date: d, key: d.toDateString(), count: 0 });
    }
    for (const e of watchEvents) {
      const key = new Date(e.createdAt).toDateString();
      const slot = days.find((d) => d.key === key);
      if (slot) slot.count += 1;
    }
    return days;
  }, [watchEvents]);

  const pointsFromWatching = watchEvents.reduce((acc, e) => acc + e.pointsReceived, 0);

  if (authLoading || (!user && isAuthenticated)) {
    return <Skeleton className="h-screen w-full" />;
  }
  if (!user) return null;

  return (
    <div className="min-h-screen">
      {/* Header */}
      <div className="bg-gradient-to-b from-primary/15 via-background to-transparent">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
          <Link
            href="/profile"
            className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground mb-4 text-sm transition-colors"
          >
            <ArrowLeft className="h-4 w-4" />
            Back to profile
          </Link>

          <div className="flex items-center gap-4">
            <div className="rounded-xl bg-primary/15 p-3">
              <History className="h-7 w-7 text-primary" />
            </div>
            <div>
              <h1 className="text-3xl sm:text-4xl font-bold text-foreground">
                Your watch activity
              </h1>
              <p className="text-muted-foreground mt-1">
                Every movie you watch is logged and earns you points.
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        {/* Stat cards */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <StatCard
            icon={Film}
            value={watchEvents.length}
            label="Movies watched"
            tone="primary"
          />
          <StatCard
            icon={Trophy}
            value={formatPoints(pointsFromWatching)}
            label="Points from watching"
            tone="accent"
          />
          <StatCard
            icon={Flame}
            value={streak}
            label={streak === 1 ? "Day streak" : "Day streak"}
            tone="orange"
          />
          <StatCard
            icon={Sparkles}
            value={reviewEvents.length}
            label="Reviews written"
            tone="primary"
          />
        </div>

        {/* Activity strip */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-base">
              <Calendar className="h-4 w-4 text-primary" />
              Last 14 days
            </CardTitle>
            <CardDescription>Daily watch activity</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-7 sm:grid-cols-14 gap-1.5">
              {past14Days.map((d) => {
                const intensity =
                  d.count === 0
                    ? "bg-secondary/40"
                    : d.count === 1
                    ? "bg-primary/30"
                    : d.count === 2
                    ? "bg-primary/55"
                    : "bg-primary";
                return (
                  <div
                    key={d.key}
                    className="aspect-square rounded-md flex flex-col items-center justify-center group relative"
                    title={`${d.date.toLocaleDateString()} • ${d.count} watch${d.count === 1 ? "" : "es"}`}
                  >
                    <div
                      className={`absolute inset-0 rounded-md transition-transform group-hover:scale-110 ${intensity}`}
                    />
                    <span className="relative text-[10px] font-semibold text-foreground/80">
                      {d.date.getDate()}
                    </span>
                  </div>
                );
              })}
            </div>
            <div className="mt-3 flex items-center justify-between text-xs text-muted-foreground">
              <span>14 days ago</span>
              <div className="flex items-center gap-1.5">
                <span>Less</span>
                <div className="h-3 w-3 rounded-sm bg-secondary/40" />
                <div className="h-3 w-3 rounded-sm bg-primary/30" />
                <div className="h-3 w-3 rounded-sm bg-primary/55" />
                <div className="h-3 w-3 rounded-sm bg-primary" />
                <span>More</span>
              </div>
              <span>Today</span>
            </div>
          </CardContent>
        </Card>

        {/* Timeline */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-base">
              <Clock className="h-4 w-4 text-primary" />
              Full timeline
            </CardTitle>
            <CardDescription>
              Each movie watched, ordered newest to oldest
            </CardDescription>
          </CardHeader>
          <CardContent>
            {loading ? (
              <div className="space-y-2">
                {Array.from({ length: 6 }).map((_, i) => (
                  <Skeleton key={i} className="h-14" />
                ))}
              </div>
            ) : watchEvents.length > 0 ? (
              <ol className="relative border-l-2 border-border/50 ml-3 space-y-4 py-2">
                {watchEvents
                  .slice()
                  .sort(
                    (a, b) =>
                      new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
                  )
                  .slice(0, 30)
                  .map((event) => (
                    <li key={event.id} className="ml-6">
                      <span className="absolute -left-[9px] flex h-4 w-4 items-center justify-center rounded-full bg-primary ring-4 ring-background">
                        <Film className="h-2.5 w-2.5 text-primary-foreground" />
                      </span>
                      <div className="flex items-center justify-between rounded-lg bg-secondary/40 px-4 py-2.5">
                        <div>
                          <p className="text-sm font-medium text-foreground">
                            Marked a movie as watched
                          </p>
                          <p className="text-xs text-muted-foreground">
                            {new Date(event.createdAt).toLocaleString(undefined, {
                              dateStyle: "medium",
                              timeStyle: "short",
                            })}
                          </p>
                        </div>
                        <Badge variant="accent">+{event.pointsReceived}</Badge>
                      </div>
                    </li>
                  ))}
              </ol>
            ) : (
              <div className="py-10 text-center">
                <Film className="h-10 w-10 text-muted-foreground/40 mx-auto mb-3" />
                <p className="font-medium text-foreground">No watches logged yet</p>
                <p className="text-sm text-muted-foreground mt-1 mb-4">
                  Hit "Mark as Watched" on any movie to start earning points.
                </p>
                <Link href="/movies">
                  <Button>Browse movies</Button>
                </Link>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

function StatCard({
  icon: Icon,
  value,
  label,
  tone,
}: {
  icon: typeof Film;
  value: number | string;
  label: string;
  tone: "primary" | "accent" | "orange";
}) {
  const tones = {
    primary: "bg-primary/15 text-primary",
    accent: "bg-accent/20 text-accent",
    orange: "bg-orange-500/20 text-orange-400",
  };
  return (
    <Card>
      <CardContent className="pt-6">
        <div className="flex items-center gap-3">
          <div className={`rounded-lg p-2.5 ${tones[tone]}`}>
            <Icon className="h-5 w-5" />
          </div>
          <div>
            <p className="text-2xl font-bold text-foreground">{value}</p>
            <p className="text-xs text-muted-foreground">{label}</p>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
