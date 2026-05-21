"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";
import { rewardService, redemptionService } from "@/services/reward.service";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Dialog } from "@/components/ui/dialog";
import { RewardCard } from "@/components/rewards/reward-card";
import { useToast } from "@/components/ui/toast";
import { formatPoints } from "@/lib/utils";
import {
  ArrowLeft,
  Coins,
  Filter,
  Gift,
  History,
  Loader2,
  Sparkles,
  Trophy,
  TrendingUp,
  CheckCircle2,
} from "lucide-react";
import type { Redemption, Reward, RewardType } from "@/types";

const FILTERS: { value: "ALL" | "AFFORDABLE" | RewardType; label: string }[] = [
  { value: "ALL", label: "All" },
  { value: "AFFORDABLE", label: "I can afford" },
  { value: "GIFT_CARD", label: "Gift cards" },
  { value: "CASH_PAYOUT", label: "Cash payouts" },
  { value: "DISCOUNT_CODE", label: "Discount codes" },
];

export default function RewardsPage() {
  const router = useRouter();
  const toast = useToast();
  const { user, isAuthenticated, isLoading: authLoading, refreshUser } = useAuth();

  const [rewards, setRewards] = useState<Reward[]>([]);
  const [redemptions, setRedemptions] = useState<Redemption[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<(typeof FILTERS)[number]["value"]>("ALL");
  const [selected, setSelected] = useState<Reward | null>(null);
  const [redeeming, setRedeeming] = useState(false);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push("/login?next=/rewards");
    }
  }, [authLoading, isAuthenticated, router]);

  const fetchData = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    try {
      const [rewardsData, redemptionsData] = await Promise.all([
        rewardService.getActiveRewards().catch(() => []),
        redemptionService.getUserRedemptions(user.id).catch(() => [] as Redemption[]),
      ]);
      setRewards(Array.isArray(rewardsData) ? rewardsData : []);
      setRedemptions(Array.isArray(redemptionsData) ? redemptionsData : []);
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const visible = useMemo(() => {
    if (!user) return [] as Reward[];
    let list = rewards;
    if (filter === "AFFORDABLE") {
      list = list.filter((r) => user.totalPoints >= r.pointCost && r.stock > 0);
    } else if (filter !== "ALL") {
      list = list.filter((r) => r.type === filter);
    }
    return list.slice().sort((a, b) => a.pointCost - b.pointCost);
  }, [rewards, filter, user]);

  const cheapestAffordable = useMemo(() => {
    if (!user) return null;
    return (
      rewards
        .filter((r) => r.stock > 0 && r.active)
        .slice()
        .sort((a, b) => a.pointCost - b.pointCost)
        .find((r) => r.pointCost > user.totalPoints) ?? null
    );
  }, [rewards, user]);

  const handleRedeem = async () => {
    if (!user || !selected) return;
    setRedeeming(true);
    try {
      const redemption = await redemptionService.redeem(user.id, selected.id);
      toast.pointsEarned(
        -selected.pointCost,
        `Redeemed: ${selected.name}`
      );
      toast.success("Reward unlocked", "Your redemption is on its way.");
      setRedemptions((prev) => [redemption, ...(Array.isArray(prev) ? prev : [])]);
      setSelected(null);
      await Promise.all([refreshUser(), fetchData()]);
    } catch (err) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        "Redemption failed. Try again in a moment.";
      toast.error("Could not redeem", message);
    } finally {
      setRedeeming(false);
    }
  };

  if (authLoading || (!user && isAuthenticated)) {
    return <RewardsSkeleton />;
  }

  if (!user) return null;

  return (
    <div className="min-h-screen">
      {/* Header */}
      <div className="relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-accent/20 via-primary/10 to-background" />
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,_var(--tw-gradient-stops))] from-primary/15 via-transparent to-transparent" />

        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <Link
            href="/profile"
            className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground mb-6 transition-colors text-sm"
          >
            <ArrowLeft className="h-4 w-4" />
            Back to profile
          </Link>

          <div className="grid lg:grid-cols-[1fr_360px] gap-8 items-end">
            <div>
              <div className="inline-flex items-center gap-2 rounded-full border border-accent/30 bg-accent/10 px-3 py-1 text-xs font-medium text-accent mb-4">
                <Sparkles className="h-3.5 w-3.5" />
                Reward marketplace
              </div>
              <h1 className="text-4xl sm:text-5xl font-bold text-foreground mb-3">
                Spend points,
                <br />
                <span className="bg-gradient-to-r from-accent to-primary bg-clip-text text-transparent">
                  unlock real value
                </span>
              </h1>
              <p className="text-muted-foreground text-lg max-w-2xl">
                Convert your hard-earned activity points into gift cards, payouts, and exclusive
                discount codes. Watch movies and write reviews to earn more.
              </p>
            </div>

            {/* Wallet card */}
            <div className="rounded-2xl border border-accent/30 bg-gradient-to-br from-accent/10 via-background to-card p-6 shadow-2xl">
              <div className="flex items-center justify-between mb-4">
                <span className="text-xs uppercase tracking-wider text-muted-foreground">
                  Your wallet
                </span>
                <Trophy className="h-5 w-5 text-accent" />
              </div>
              <div className="flex items-baseline gap-2 mb-1">
                <span className="text-4xl font-bold text-accent">
                  {formatPoints(user.totalPoints)}
                </span>
                <span className="text-sm text-muted-foreground">points</span>
              </div>
              {cheapestAffordable && (
                <p className="text-xs text-muted-foreground mb-4">
                  {(cheapestAffordable.pointCost - user.totalPoints).toLocaleString()} more
                  to unlock <span className="font-medium text-foreground">{cheapestAffordable.name}</span>
                </p>
              )}
              <div className="grid grid-cols-3 gap-2 pt-4 border-t border-border">
                <div className="text-center">
                  <p className="text-lg font-bold text-foreground">
                    {redemptions.length}
                  </p>
                  <p className="text-[10px] uppercase tracking-wider text-muted-foreground">
                    Redeemed
                  </p>
                </div>
                <div className="text-center">
                  <p className="text-lg font-bold text-foreground">
                    {rewards.length}
                  </p>
                  <p className="text-[10px] uppercase tracking-wider text-muted-foreground">
                    Available
                  </p>
                </div>
                <div className="text-center">
                  <p className="text-lg font-bold text-foreground">
                    {rewards.filter((r) => user.totalPoints >= r.pointCost && r.stock > 0).length}
                  </p>
                  <p className="text-[10px] uppercase tracking-wider text-muted-foreground">
                    In reach
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 grid lg:grid-cols-[1fr_320px] gap-10">
        <div>
          {/* Filters */}
          <div className="flex flex-wrap items-center gap-2 mb-6">
            <div className="inline-flex items-center gap-2 text-muted-foreground text-sm mr-2">
              <Filter className="h-4 w-4" />
              Filter
            </div>
            {FILTERS.map((f) => (
              <Badge
                key={f.value}
                variant={filter === f.value ? "accent" : "secondary"}
                onClick={() => setFilter(f.value)}
                className="cursor-pointer select-none px-3 py-1 text-sm hover:scale-105 transition-transform"
              >
                {f.label}
              </Badge>
            ))}
          </div>

          {/* Grid */}
          {loading ? (
            <div className="grid sm:grid-cols-2 xl:grid-cols-3 gap-5">
              {Array.from({ length: 6 }).map((_, i) => (
                <Skeleton key={i} className="h-72 rounded-2xl" />
              ))}
            </div>
          ) : visible.length > 0 ? (
            <div className="grid sm:grid-cols-2 xl:grid-cols-3 gap-5">
              {visible.map((reward) => (
                <RewardCard
                  key={reward.id}
                  reward={reward}
                  userPoints={user.totalPoints}
                  onRedeem={(r) => setSelected(r)}
                />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="py-16 text-center">
                <Gift className="h-12 w-12 text-muted-foreground mx-auto mb-4 opacity-50" />
                <h3 className="font-semibold text-foreground mb-1">No rewards here yet</h3>
                <p className="text-sm text-muted-foreground">
                  Try a different filter, or check back later.
                </p>
              </CardContent>
            </Card>
          )}
        </div>

        {/* Side rail */}
        <aside className="space-y-6">
          {/* Earn more */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-base">
                <TrendingUp className="h-4 w-4 text-primary" />
                Earn more points
              </CardTitle>
              <CardDescription>Fast ways to top up your wallet</CardDescription>
            </CardHeader>
            <CardContent className="space-y-2">
              <Link
                href="/movies"
                className="flex items-center justify-between rounded-lg border border-border bg-secondary/40 px-3 py-2 hover:border-primary/40 transition-colors"
              >
                <div>
                  <p className="text-sm font-medium text-foreground">Watch a movie</p>
                  <p className="text-xs text-muted-foreground">Earn 15 pts each</p>
                </div>
                <Badge variant="accent">+15</Badge>
              </Link>
              <Link
                href="/movies"
                className="flex items-center justify-between rounded-lg border border-border bg-secondary/40 px-3 py-2 hover:border-primary/40 transition-colors"
              >
                <div>
                  <p className="text-sm font-medium text-foreground">Write a review</p>
                  <p className="text-xs text-muted-foreground">Earn 10 pts each</p>
                </div>
                <Badge variant="accent">+10</Badge>
              </Link>
              <Link
                href="/survey"
                className="flex items-center justify-between rounded-lg border border-primary/30 bg-primary/10 px-3 py-2 hover:border-primary/60 transition-colors"
              >
                <div>
                  <p className="text-sm font-medium text-foreground">Tune your taste profile</p>
                  <p className="text-xs text-muted-foreground">Better AI picks</p>
                </div>
                <Badge variant="default">Open</Badge>
              </Link>
            </CardContent>
          </Card>

          {/* History */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-base">
                <History className="h-4 w-4 text-primary" />
                Recent redemptions
              </CardTitle>
              <CardDescription>Your last reward unlocks</CardDescription>
            </CardHeader>
            <CardContent>
              {loading ? (
                <div className="space-y-2">
                  {Array.from({ length: 3 }).map((_, i) => (
                    <Skeleton key={i} className="h-14 w-full rounded-lg" />
                  ))}
                </div>
              ) : redemptions.length > 0 ? (
                <ul className="space-y-2">
                  {redemptions.slice(0, 6).map((r) => (
                    <li
                      key={r.id}
                      className="flex items-center justify-between rounded-lg bg-secondary/40 px-3 py-2"
                    >
                      <div className="min-w-0">
                        <p className="text-sm font-medium text-foreground truncate">
                          {r.rewardName ?? `Reward #${r.rewardId}`}
                        </p>
                        <p className="text-xs text-muted-foreground">
                          {r.createdAt
                            ? new Date(r.createdAt).toLocaleDateString()
                            : "Recent"}
                        </p>
                      </div>
                      <div className="flex items-center gap-1.5">
                        <Coins className="h-3.5 w-3.5 text-accent" />
                        <span className="text-sm font-semibold text-accent">
                          {r.pointsSpent ?? 0}
                        </span>
                      </div>
                    </li>
                  ))}
                </ul>
              ) : (
                <div className="text-center py-6 text-muted-foreground text-sm">
                  No redemptions yet — pick your first reward!
                </div>
              )}
            </CardContent>
          </Card>
        </aside>
      </div>

      {/* Confirm modal */}
      <Dialog
        open={selected !== null}
        onClose={() => !redeeming && setSelected(null)}
        title="Confirm redemption"
        description={
          selected
            ? `You're about to spend ${selected.pointCost.toLocaleString()} points on this reward.`
            : ""
        }
      >
        {selected && (
          <div className="space-y-4">
            <div className="rounded-xl border border-border bg-secondary/40 p-4">
              <div className="flex items-center justify-between mb-2">
                <h4 className="font-semibold text-foreground">{selected.name}</h4>
                <Badge variant="accent">
                  <Coins className="h-3 w-3 mr-1" />
                  {selected.pointCost.toLocaleString()}
                </Badge>
              </div>
              {selected.description && (
                <p className="text-sm text-muted-foreground">{selected.description}</p>
              )}
            </div>

            <div className="flex items-center justify-between text-sm">
              <span className="text-muted-foreground">Balance after redemption</span>
              <span className="font-semibold text-foreground">
                {formatPoints(Math.max(0, user.totalPoints - selected.pointCost))} pts
              </span>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button
                variant="ghost"
                onClick={() => setSelected(null)}
                disabled={redeeming}
              >
                Cancel
              </Button>
              <Button onClick={handleRedeem} disabled={redeeming} className="gap-2">
                {redeeming ? (
                  <>
                    <Loader2 className="h-4 w-4 animate-spin" />
                    Redeeming…
                  </>
                ) : (
                  <>
                    <CheckCircle2 className="h-4 w-4" />
                    Confirm redemption
                  </>
                )}
              </Button>
            </div>
          </div>
        )}
      </Dialog>
    </div>
  );
}

function RewardsSkeleton() {
  return (
    <div className="min-h-screen">
      <div className="bg-gradient-to-b from-accent/10 to-transparent">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 grid lg:grid-cols-[1fr_360px] gap-8">
          <div className="space-y-4">
            <Skeleton className="h-4 w-32" />
            <Skeleton className="h-14 w-3/4" />
            <Skeleton className="h-4 w-2/3" />
          </div>
          <Skeleton className="h-44 rounded-2xl" />
        </div>
      </div>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 grid lg:grid-cols-[1fr_320px] gap-10">
        <div className="grid sm:grid-cols-2 xl:grid-cols-3 gap-5">
          {Array.from({ length: 6 }).map((_, i) => (
            <Skeleton key={i} className="h-72 rounded-2xl" />
          ))}
        </div>
        <Skeleton className="h-80 rounded-2xl" />
      </div>
    </div>
  );
}
