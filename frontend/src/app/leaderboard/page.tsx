"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { userService } from "@/services/user.service";
import { useAuth } from "@/contexts/auth-context";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { formatPoints, getInitials } from "@/lib/utils";
import { Trophy, Medal, Crown, User, ArrowLeft } from "lucide-react";
import type { LeaderboardEntry } from "@/types";

export default function LeaderboardPage() {
  const { user } = useAuth();
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [userRank, setUserRank] = useState<number | null>(null);

  useEffect(() => {
    const fetchLeaderboard = async () => {
      try {
        const data = await userService.getLeaderboard();
        setLeaderboard(data);

        if (user) {
          const rank = data.findIndex((entry) => entry.userId === user.id);
          if (rank !== -1) {
            setUserRank(rank + 1);
          }
        }
      } catch (error) {
        console.error("Failed to fetch leaderboard:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchLeaderboard();
  }, [user]);

  const getRankIcon = (rank: number) => {
    switch (rank) {
      case 1:
        return <Crown className="h-6 w-6 text-yellow-500" />;
      case 2:
        return <Medal className="h-6 w-6 text-gray-400" />;
      case 3:
        return <Medal className="h-6 w-6 text-amber-600" />;
      default:
        return null;
    }
  };

  const getRankStyle = (rank: number) => {
    switch (rank) {
      case 1:
        return "bg-gradient-to-r from-yellow-500/10 to-yellow-600/10 border-yellow-500/30";
      case 2:
        return "bg-gradient-to-r from-gray-400/10 to-gray-500/10 border-gray-400/30";
      case 3:
        return "bg-gradient-to-r from-amber-600/10 to-amber-700/10 border-amber-600/30";
      default:
        return "bg-card border-border";
    }
  };

  return (
    <div className="min-h-screen">
      {/* Header */}
      <div className="bg-gradient-to-b from-accent/10 to-transparent">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <Link
            href="/"
            className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground mb-6 transition-colors"
          >
            <ArrowLeft className="h-4 w-4" />
            Back to Home
          </Link>

          <div className="flex items-center gap-4">
            <div className="p-3 bg-accent/10 rounded-full">
              <Trophy className="h-10 w-10 text-accent" />
            </div>
            <div>
              <h1 className="text-3xl sm:text-4xl font-bold text-foreground">
                Leaderboard
              </h1>
              <p className="text-muted-foreground mt-1">
                Top movie enthusiasts ranked by points
              </p>
            </div>
          </div>

          {user && userRank && (
            <div className="mt-6 p-4 bg-primary/10 rounded-lg border border-primary/20">
              <p className="text-sm text-muted-foreground">Your Rank</p>
              <p className="text-2xl font-bold text-primary">
                #{userRank} with {formatPoints(user.totalPoints)} points
              </p>
            </div>
          )}
        </div>
      </div>

      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Top 3 Podium */}
        {!isLoading && leaderboard.length >= 3 && (
          <div className="grid grid-cols-3 gap-4 mb-8">
            {/* 2nd Place */}
            <div className="order-1 pt-8">
              <Card className={`text-center ${getRankStyle(2)}`}>
                <CardContent className="pt-6">
                  <div className="flex justify-center mb-3">
                    <Medal className="h-8 w-8 text-gray-400" />
                  </div>
                  <div className="h-12 w-12 rounded-full bg-gray-400/10 border-2 border-gray-400/30 flex items-center justify-center mx-auto mb-2">
                    <span className="text-lg font-bold text-gray-400">
                      {getInitials(leaderboard[1].username)}
                    </span>
                  </div>
                  <p className="font-semibold text-foreground text-sm truncate">
                    {leaderboard[1].username}
                  </p>
                  <p className="text-lg font-bold text-gray-400">
                    {formatPoints(leaderboard[1].totalPoints)}
                  </p>
                </CardContent>
              </Card>
            </div>

            {/* 1st Place */}
            <div className="order-2">
              <Card className={`text-center ${getRankStyle(1)}`}>
                <CardContent className="pt-6">
                  <div className="flex justify-center mb-3">
                    <Crown className="h-10 w-10 text-yellow-500" />
                  </div>
                  <div className="h-14 w-14 rounded-full bg-yellow-500/10 border-2 border-yellow-500/30 flex items-center justify-center mx-auto mb-2">
                    <span className="text-xl font-bold text-yellow-500">
                      {getInitials(leaderboard[0].username)}
                    </span>
                  </div>
                  <p className="font-semibold text-foreground truncate">
                    {leaderboard[0].username}
                  </p>
                  <p className="text-xl font-bold text-yellow-500">
                    {formatPoints(leaderboard[0].totalPoints)}
                  </p>
                </CardContent>
              </Card>
            </div>

            {/* 3rd Place */}
            <div className="order-3 pt-12">
              <Card className={`text-center ${getRankStyle(3)}`}>
                <CardContent className="pt-6">
                  <div className="flex justify-center mb-3">
                    <Medal className="h-7 w-7 text-amber-600" />
                  </div>
                  <div className="h-11 w-11 rounded-full bg-amber-600/10 border-2 border-amber-600/30 flex items-center justify-center mx-auto mb-2">
                    <span className="text-base font-bold text-amber-600">
                      {getInitials(leaderboard[2].username)}
                    </span>
                  </div>
                  <p className="font-semibold text-foreground text-sm truncate">
                    {leaderboard[2].username}
                  </p>
                  <p className="text-base font-bold text-amber-600">
                    {formatPoints(leaderboard[2].totalPoints)}
                  </p>
                </CardContent>
              </Card>
            </div>
          </div>
        )}

        {/* Full Leaderboard */}
        <Card>
          <CardHeader>
            <CardTitle>All Rankings</CardTitle>
            <CardDescription>Complete leaderboard of all users</CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="space-y-3">
                {Array.from({ length: 10 }).map((_, i) => (
                  <Skeleton key={i} className="h-16 w-full" />
                ))}
              </div>
            ) : leaderboard.length > 0 ? (
              <div className="space-y-2">
                {leaderboard.map((entry) => (
                  <div
                    key={entry.userId}
                    className={`flex items-center justify-between p-4 rounded-lg border transition-colors ${
                      user?.id === entry.userId
                        ? "bg-primary/10 border-primary/30"
                        : getRankStyle(entry.rank)
                    }`}
                  >
                    <div className="flex items-center gap-4">
                      <div className="w-8 text-center">
                        {getRankIcon(entry.rank) || (
                          <span className="text-lg font-bold text-muted-foreground">
                            {entry.rank}
                          </span>
                        )}
                      </div>
                      
                      <div className="h-10 w-10 rounded-full bg-secondary flex items-center justify-center">
                        <span className="text-sm font-semibold">
                          {getInitials(entry.username)}
                        </span>
                      </div>
                      
                      <div>
                        <p className="font-semibold text-foreground flex items-center gap-2">
                          {entry.username}
                          {user?.id === entry.userId && (
                            <Badge variant="secondary" className="text-xs">You</Badge>
                          )}
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-2">
                      <Trophy className="h-4 w-4 text-accent" />
                      <span className="font-bold text-accent">
                        {formatPoints(entry.totalPoints)}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-12 text-muted-foreground">
                <User className="h-12 w-12 mx-auto mb-4 opacity-50" />
                <p>No users yet</p>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
