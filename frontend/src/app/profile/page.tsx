"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuth } from "@/contexts/auth-context";
import { userService } from "@/services/user.service";
import { rewardService, redemptionService } from "@/services/reward.service";
import { reviewService } from "@/services/review.service";
import { surveyService } from "@/services/survey.service";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { formatPoints, getInitials } from "@/lib/utils";
import {
  Trophy,
  Star,
  Film,
  MessageSquare,
  Gift,
  Calendar,
  TrendingUp,
  Award,
  Sparkles,
  Wand2,
  History,
  ChevronRight,
  Coins,
} from "lucide-react";
import type { PointHistory, Redemption, Reward, Review } from "@/types";

export default function ProfilePage() {
  const router = useRouter();
  const { user, isAuthenticated, isLoading: authLoading } = useAuth();
  const [pointHistory, setPointHistory] = useState<PointHistory[]>([]);
  const [rewards, setRewards] = useState<Reward[]>([]);
  const [userReviews, setUserReviews] = useState<Review[]>([]);
  const [redemptions, setRedemptions] = useState<Redemption[]>([]);
  const [hasSurvey, setHasSurvey] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push("/login");
    }
  }, [authLoading, isAuthenticated, router]);

  useEffect(() => {
    const fetchData = async () => {
      if (!user) return;
      setIsLoading(true);
      try {
        const [historyData, rewardsData, reviewsData, redemptionData, surveyData] =
          await Promise.all([
            userService.getPointHistory(user.id).catch(() => []),
            rewardService.getActiveRewards().catch(() => []),
            reviewService.getReviewsByUser(user.id).catch(() => []),
            redemptionService.getUserRedemptions(user.id).catch(() => []),
            surveyService.getSurvey(user.id).catch(() => null),
          ]);
        setPointHistory(historyData);
        setRewards(rewardsData);
        setUserReviews(reviewsData);
        setRedemptions(redemptionData);
        setHasSurvey(Boolean(surveyData?.exists));
      } finally {
        setIsLoading(false);
      }
    };
    if (user) fetchData();
  }, [user]);

  const watches = useMemo(
    () => pointHistory.filter((p) => p.eventType === "WATCH_MOVIE").length,
    [pointHistory]
  );

  const affordableRewards = useMemo(() => {
    if (!user) return [] as Reward[];
    return rewards
      .filter((r) => user.totalPoints >= r.pointCost && r.stock > 0 && r.active)
      .slice()
      .sort((a, b) => b.pointCost - a.pointCost);
  }, [rewards, user]);

  const getEventIcon = (eventType: string) => {
    switch (eventType) {
      case "WATCH_MOVIE":
        return <Film className="h-4 w-4 text-primary" />;
      case "WRITE_REVIEW":
        return <MessageSquare className="h-4 w-4 text-accent" />;
      case "FIRST_LOGIN":
        return <Gift className="h-4 w-4 text-primary" />;
      case "REWARD_REDEMPTION_BONUS":
        return <Award className="h-4 w-4 text-accent" />;
      default:
        return <Star className="h-4 w-4 text-muted-foreground" />;
    }
  };

  const getEventLabel = (eventType: string) => {
    switch (eventType) {
      case "WATCH_MOVIE":
        return "Watched a Movie";
      case "WRITE_REVIEW":
        return "Wrote a Review";
      case "FIRST_LOGIN":
        return "First Login Bonus";
      case "REWARD_REDEMPTION_BONUS":
        return "Redemption Bonus";
      default:
        return eventType;
    }
  };

  if (authLoading || (!user && isAuthenticated)) {
    return <ProfileSkeleton />;
  }
  if (!user) return null;

  return (
    <div className="min-h-screen">
      <div className="bg-gradient-to-b from-primary/10 to-transparent">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="flex flex-col sm:flex-row items-start sm:items-center gap-6">
            <div className="h-24 w-24 rounded-full bg-primary/10 border-4 border-primary/20 flex items-center justify-center">
              <span className="text-3xl font-bold text-primary">
                {getInitials(user.username)}
              </span>
            </div>

            <div className="flex-1">
              <h1 className="text-3xl font-bold text-foreground mb-1">{user.username}</h1>
              <p className="text-muted-foreground mb-4">{user.email}</p>

              <div className="flex flex-wrap items-center gap-3">
                <Link
                  href="/rewards"
                  className="flex items-center gap-2 px-4 py-2 bg-accent/10 rounded-full hover:bg-accent/20 transition-colors"
                >
                  <Trophy className="h-5 w-5 text-accent" />
                  <span className="text-lg font-bold text-accent">
                    {formatPoints(user.totalPoints)} pts
                  </span>
                </Link>

                <Link href="/leaderboard">
                  <Badge variant="secondary" className="cursor-pointer">
                    <TrendingUp className="h-3 w-3 mr-1" />
                    Leaderboard
                  </Badge>
                </Link>

                <Link href="/survey">
                  <Badge
                    variant={hasSurvey ? "accent" : "default"}
                    className="cursor-pointer"
                  >
                    <Wand2 className="h-3 w-3 mr-1" />
                    {hasSurvey ? "Profile set" : "Build profile"}
                  </Badge>
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
        {/* Quick links */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <QuickAction
            href="/recommendations"
            icon={Sparkles}
            label="AI Picks"
            hint="Fresh suggestions"
            tone="primary"
          />
          <QuickAction
            href="/rewards"
            icon={Gift}
            label="Rewards"
            hint={`${affordableRewards.length} ready`}
            tone="accent"
          />
          <QuickAction
            href="/watch-history"
            icon={History}
            label="Watch History"
            hint={`${watches} watched`}
            tone="primary"
          />
          <QuickAction
            href="/movies"
            icon={Film}
            label="Browse"
            hint="Find something new"
            tone="accent"
          />
        </div>

        {/* Stats Cards */}
        <div className="grid sm:grid-cols-3 gap-4">
          <Card>
            <CardContent className="pt-6">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-accent/10 rounded-full">
                  <Trophy className="h-6 w-6 text-accent" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-foreground">
                    {formatPoints(user.totalPoints)}
                  </p>
                  <p className="text-sm text-muted-foreground">Total Points</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="pt-6">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-primary/10 rounded-full">
                  <MessageSquare className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-foreground">{userReviews.length}</p>
                  <p className="text-sm text-muted-foreground">Reviews Written</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="pt-6">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-primary/10 rounded-full">
                  <Film className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-foreground">{watches}</p>
                  <p className="text-sm text-muted-foreground">Movies Watched</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="grid lg:grid-cols-2 gap-8">
          {/* Points History */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Calendar className="h-5 w-5" />
                Points History
              </CardTitle>
              <CardDescription>Your recent point-earning activities</CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <div className="space-y-4">
                  {Array.from({ length: 5 }).map((_, i) => (
                    <Skeleton key={i} className="h-16 w-full" />
                  ))}
                </div>
              ) : pointHistory.length > 0 ? (
                <div className="space-y-3 max-h-96 overflow-y-auto pr-1">
                  {pointHistory.map((event, index) => (
                    <div
                      key={event.id || index}
                      className="flex items-center justify-between p-3 bg-secondary/50 rounded-lg"
                    >
                      <div className="flex items-center gap-3">
                        <div className="p-2 bg-background rounded-full">
                          {getEventIcon(event.eventType)}
                        </div>
                        <div>
                          <p className="font-medium text-foreground">
                            {getEventLabel(event.eventType)}
                          </p>
                          <p className="text-xs text-muted-foreground">
                            {new Date(event.createdAt).toLocaleDateString()}
                          </p>
                        </div>
                      </div>
                      <Badge variant="accent" className="font-semibold">
                        +{event.pointsReceived}
                      </Badge>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-8 text-muted-foreground">
                  <Trophy className="h-12 w-12 mx-auto mb-4 opacity-50" />
                  <p>No point history yet</p>
                  <p className="text-sm mt-1">Start watching and reviewing!</p>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Rewards in reach */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Gift className="h-5 w-5" />
                Rewards in reach
              </CardTitle>
              <CardDescription>
                You can redeem these right now
              </CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <div className="space-y-3">
                  {Array.from({ length: 3 }).map((_, i) => (
                    <Skeleton key={i} className="h-20 w-full" />
                  ))}
                </div>
              ) : affordableRewards.length > 0 ? (
                <div className="space-y-3">
                  {affordableRewards.slice(0, 4).map((reward) => (
                    <div
                      key={reward.id}
                      className="p-4 border border-border rounded-lg hover:border-primary/50 transition-colors"
                    >
                      <div className="flex items-start justify-between mb-1">
                        <h4 className="font-semibold text-foreground">{reward.name}</h4>
                        <Badge variant="accent">
                          <Coins className="h-3 w-3 mr-1" />
                          {reward.pointCost}
                        </Badge>
                      </div>
                      {reward.description && (
                        <p className="text-sm text-muted-foreground line-clamp-2">
                          {reward.description}
                        </p>
                      )}
                    </div>
                  ))}
                  <Link href="/rewards">
                    <Button variant="outline" className="w-full mt-2 gap-2">
                      Open reward marketplace
                      <ChevronRight className="h-4 w-4" />
                    </Button>
                  </Link>
                </div>
              ) : (
                <div className="text-center py-8 text-muted-foreground">
                  <Gift className="h-12 w-12 mx-auto mb-3 opacity-50" />
                  <p>Keep earning to unlock rewards.</p>
                  <Link href="/rewards" className="mt-3 inline-block">
                    <Button variant="outline" size="sm">
                      See all rewards
                    </Button>
                  </Link>
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Recent Reviews */}
        {userReviews.length > 0 && (
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <MessageSquare className="h-5 w-5" />
                Your Reviews
              </CardTitle>
              <CardDescription>Reviews you've written</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {userReviews.slice(0, 5).map((review, index) => (
                  <div
                    key={review.id || index}
                    className="p-4 bg-secondary/50 rounded-lg"
                  >
                    <div className="flex items-start justify-between mb-2">
                      <h4 className="font-semibold text-foreground">
                        {review.title || "Untitled"}
                      </h4>
                      <div className="flex items-center gap-1">
                        <Star className="h-4 w-4 fill-accent text-accent" />
                        <span className="text-sm font-medium">
                          {review.rating_score}
                        </span>
                      </div>
                    </div>
                    {review.body && (
                      <p className="text-sm text-muted-foreground line-clamp-2">
                        {review.body}
                      </p>
                    )}
                    <Link href={`/movies/${review.movieId}`}>
                      <Button variant="link" size="sm" className="px-0 mt-2">
                        View Movie
                      </Button>
                    </Link>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}

        {/* Redemption history */}
        {redemptions.length > 0 && (
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Award className="h-5 w-5" />
                Redemption history
              </CardTitle>
              <CardDescription>Every reward you've unlocked</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                {redemptions.map((r) => (
                  <div
                    key={r.id}
                    className="flex items-center justify-between p-3 bg-secondary/40 rounded-lg"
                  >
                    <div>
                      <p className="font-medium text-foreground">
                        {r.rewardName ?? `Reward #${r.rewardId}`}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        {r.createdAt
                          ? new Date(r.createdAt).toLocaleString()
                          : "Recent"}
                      </p>
                    </div>
                    <div className="flex items-center gap-3">
                      {r.status && (
                        <Badge variant={r.status === "FULFILLED" ? "accent" : "secondary"}>
                          {r.status}
                        </Badge>
                      )}
                      <Badge variant="default">
                        <Coins className="h-3 w-3 mr-1" />
                        {r.pointsSpent ?? 0}
                      </Badge>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}

function QuickAction({
  href,
  icon: Icon,
  label,
  hint,
  tone,
}: {
  href: string;
  icon: typeof Sparkles;
  label: string;
  hint: string;
  tone: "primary" | "accent";
}) {
  const styles =
    tone === "primary"
      ? "border-primary/30 bg-primary/5 hover:bg-primary/10"
      : "border-accent/30 bg-accent/5 hover:bg-accent/10";
  const iconStyles =
    tone === "primary" ? "text-primary bg-primary/15" : "text-accent bg-accent/20";
  return (
    <Link
      href={href}
      className={`group flex items-center gap-3 rounded-xl border p-4 transition-colors ${styles}`}
    >
      <div className={`rounded-lg p-2 ${iconStyles}`}>
        <Icon className="h-5 w-5" />
      </div>
      <div className="flex-1 min-w-0">
        <p className="font-semibold text-foreground leading-tight">{label}</p>
        <p className="text-xs text-muted-foreground">{hint}</p>
      </div>
      <ChevronRight className="h-4 w-4 text-muted-foreground group-hover:translate-x-0.5 transition-transform" />
    </Link>
  );
}

function ProfileSkeleton() {
  return (
    <div className="min-h-screen">
      <div className="bg-gradient-to-b from-primary/10 to-transparent">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="flex items-center gap-6">
            <Skeleton className="h-24 w-24 rounded-full" />
            <div className="space-y-3">
              <Skeleton className="h-8 w-48" />
              <Skeleton className="h-4 w-32" />
              <Skeleton className="h-10 w-40" />
            </div>
          </div>
        </div>
      </div>
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid sm:grid-cols-3 gap-4 mb-8">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-24" />
          ))}
        </div>
      </div>
    </div>
  );
}
