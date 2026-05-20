"use client";

import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Award,
  BadgePercent,
  Banknote,
  Clapperboard,
  Coffee,
  Coins,
  CreditCard,
  Crown,
  CupSoda,
  DollarSign,
  Film,
  Gamepad2,
  Gift,
  Headphones,
  IceCream,
  Lock,
  Music,
  Package,
  Percent,
  Pizza,
  Popcorn,
  Sparkles,
  Star,
  Ticket,
  Trophy,
  Tv,
  Wallet,
  Wine,
  Zap,
} from "lucide-react";
import type { Reward } from "@/types";
import { cn } from "@/lib/utils";

type IconType = typeof Gift;

const TYPE_META: Record<
  Reward["type"],
  { label: string; gradient: string; ring: string; icon: IconType }
> = {
  GIFT_CARD: {
    label: "Gift Card",
    gradient: "from-emerald-500/30 via-emerald-500/10 to-transparent",
    ring: "ring-emerald-500/30",
    icon: Gift,
  },
  CASH_PAYOUT: {
    label: "Cash Payout",
    gradient: "from-yellow-500/30 via-amber-500/10 to-transparent",
    ring: "ring-amber-500/30",
    icon: DollarSign,
  },
  DISCOUNT_CODE: {
    label: "Discount Code",
    gradient: "from-purple-500/30 via-fuchsia-500/10 to-transparent",
    ring: "ring-purple-500/30",
    icon: Sparkles,
  },
};

// Map keywords found in the reward NAME to specific Lucide icons. The first
// keyword that appears in the name wins; if nothing matches, the type icon is
// used as the fallback. Order matters — more specific keywords first.
const NAME_ICON_MAP: Array<[string, IconType]> = [
  ["popcorn", Popcorn],
  ["ticket", Ticket],
  ["cinema", Clapperboard],
  ["theater", Clapperboard],
  ["movie night", Clapperboard],
  ["movie", Film],
  ["vip", Crown],
  ["premium", Crown],
  ["streaming", Tv],
  ["netflix", Tv],
  ["spotify", Music],
  ["music", Music],
  ["headphone", Headphones],
  ["audio", Headphones],
  ["coffee", Coffee],
  ["cafe", Coffee],
  ["pizza", Pizza],
  ["soda", CupSoda],
  ["drink", CupSoda],
  ["wine", Wine],
  ["ice cream", IceCream],
  ["snack", Popcorn],
  ["combo", Popcorn],
  ["game", Gamepad2],
  ["gift", Gift],
  ["cash", Banknote],
  ["payout", Wallet],
  ["card", CreditCard],
  ["discount", BadgePercent],
  ["coupon", BadgePercent],
  ["code", Percent],
  ["bonus", Sparkles],
  ["badge", Award],
  ["trophy", Trophy],
  ["star", Star],
  ["fast", Zap],
];

function pickIcon(reward: Reward): IconType {
  const lower = (reward.name ?? "").toLowerCase();
  for (const [keyword, icon] of NAME_ICON_MAP) {
    if (lower.includes(keyword)) return icon;
  }
  return TYPE_META[reward.type].icon;
}

interface RewardCardProps {
  reward: Reward;
  userPoints: number;
  onRedeem?: (reward: Reward) => void;
  disabled?: boolean;
}

export function RewardCard({ reward, userPoints, onRedeem, disabled }: RewardCardProps) {
  const meta = TYPE_META[reward.type];
  const Icon = pickIcon(reward);
  const affordable = userPoints >= reward.pointCost;
  const inStock = reward.stock > 0;
  const canRedeem = affordable && inStock && reward.active && !disabled;

  const progress = Math.min(100, (userPoints / reward.pointCost) * 100);

  return (
    <div
      className={cn(
        "group relative overflow-hidden rounded-2xl border border-border bg-card p-5 transition-all duration-300 hover:-translate-y-1 hover:shadow-2xl hover:ring-2",
        meta.ring
      )}
    >
      {/* gradient backdrop */}
      <div
        className={cn(
          "pointer-events-none absolute inset-0 bg-gradient-to-br opacity-60 group-hover:opacity-100 transition-opacity",
          meta.gradient
        )}
      />

      <div className="relative">
        <div className="flex items-start justify-between mb-3">
          <div className="flex items-center gap-2">
            <div className="rounded-lg bg-background/60 p-2 backdrop-blur">
              <Icon className="h-5 w-5 text-accent" />
            </div>
            <Badge variant="outline" className="bg-background/60 backdrop-blur">
              {meta.label}
            </Badge>
          </div>

          {!reward.active && (
            <Badge variant="destructive" className="bg-destructive/20 text-destructive border border-destructive/30">
              Inactive
            </Badge>
          )}
        </div>

        <h3 className="text-xl font-bold text-foreground leading-tight mb-1">
          {reward.name}
        </h3>

        {reward.description && (
          <p className="text-sm text-muted-foreground line-clamp-2 mb-4">
            {reward.description}
          </p>
        )}

        <div className="flex items-center gap-3 text-sm mb-4">
          {reward.monetaryValue > 0 && (
            <span className="inline-flex items-center gap-1 text-foreground">
              <DollarSign className="h-3.5 w-3.5 text-muted-foreground" />
              <span className="font-semibold">{reward.monetaryValue.toFixed(2)}</span>
              <span className="text-muted-foreground">value</span>
            </span>
          )}
          <span
            className={cn(
              "inline-flex items-center gap-1",
              inStock ? "text-muted-foreground" : "text-destructive"
            )}
          >
            <Package className="h-3.5 w-3.5" />
            <span className="font-medium">
              {inStock ? `${reward.stock} in stock` : "Out of stock"}
            </span>
          </span>
        </div>

        {/* Cost + progress bar */}
        <div className="mb-4">
          <div className="flex items-center justify-between mb-1.5">
            <span className="inline-flex items-center gap-1.5 text-sm font-semibold text-accent">
              <Coins className="h-4 w-4" />
              {reward.pointCost.toLocaleString()} pts
            </span>
            <span className="text-xs text-muted-foreground">
              {affordable ? "Ready to redeem" : `${(reward.pointCost - userPoints).toLocaleString()} more`}
            </span>
          </div>
          <div className="h-1.5 rounded-full bg-background/60 overflow-hidden">
            <div
              className={cn(
                "h-full rounded-full transition-all duration-700 ease-out",
                affordable
                  ? "bg-gradient-to-r from-accent to-primary"
                  : "bg-muted-foreground/40"
              )}
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>

        <Button
          className="w-full gap-2"
          variant={canRedeem ? "default" : "secondary"}
          onClick={() => canRedeem && onRedeem?.(reward)}
          disabled={!canRedeem}
        >
          {!reward.active ? (
            <>Disabled</>
          ) : !inStock ? (
            <>
              <Lock className="h-4 w-4" />
              Out of Stock
            </>
          ) : !affordable ? (
            <>
              <Lock className="h-4 w-4" />
              Need {(reward.pointCost - userPoints).toLocaleString()} more
            </>
          ) : (
            <>
              <Sparkles className="h-4 w-4" />
              Redeem Reward
            </>
          )}
        </Button>
      </div>
    </div>
  );
}
