"use client";

import Link from "next/link";
import { useAuth } from "@/contexts/auth-context";
import { Button } from "@/components/ui/button";
import { Film, Star, Trophy, Sparkles, Play, ArrowRight } from "lucide-react";

export default function Home() {
  const { isAuthenticated, user } = useAuth();

  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="relative min-h-[80vh] flex items-center justify-center overflow-hidden">
        {/* Background gradient */}
        <div className="absolute inset-0 bg-gradient-to-b from-primary/20 via-background to-background" />
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-primary/10 via-transparent to-transparent" />
        
        <div className="relative z-10 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="flex justify-center mb-6">
            <div className="inline-flex items-center gap-2 px-4 py-2 bg-primary/10 rounded-full border border-primary/20">
              <Sparkles className="h-4 w-4 text-primary" />
              <span className="text-sm font-medium text-primary">AI-Powered Recommendations</span>
            </div>
          </div>

          <h1 className="text-4xl sm:text-5xl md:text-6xl font-bold text-foreground mb-6 text-balance">
            Discover Your Next
            <span className="text-primary"> Favorite Movie</span>
          </h1>

          <p className="text-lg sm:text-xl text-muted-foreground max-w-2xl mx-auto mb-8 text-pretty">
            Get personalized movie recommendations powered by AI. Rate, review, and earn rewards 
            as you explore our extensive collection of films.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
            {isAuthenticated ? (
              <>
                <Link href="/movies">
                  <Button size="lg" className="gap-2 text-base">
                    <Play className="h-5 w-5" />
                    Browse Movies
                  </Button>
                </Link>
                <Link href="/profile">
                  <Button variant="outline" size="lg" className="gap-2 text-base">
                    <Trophy className="h-5 w-5" />
                    {user?.totalPoints || 0} Points
                  </Button>
                </Link>
              </>
            ) : (
              <>
                <Link href="/register">
                  <Button size="lg" className="gap-2 text-base">
                    Get Started Free
                    <ArrowRight className="h-5 w-5" />
                  </Button>
                </Link>
                <Link href="/movies">
                  <Button variant="outline" size="lg" className="gap-2 text-base">
                    <Play className="h-5 w-5" />
                    Browse Movies
                  </Button>
                </Link>
              </>
            )}
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8">
        <div className="max-w-6xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-3xl sm:text-4xl font-bold text-foreground mb-4">
              Why MovieRec?
            </h2>
            <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
              Experience movies like never before with our feature-rich platform
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {/* Feature 1 */}
            <div className="group p-6 rounded-xl bg-card border border-border hover:border-primary/50 transition-colors">
              <div className="p-3 bg-primary/10 rounded-lg w-fit mb-4 group-hover:bg-primary/20 transition-colors">
                <Sparkles className="h-6 w-6 text-primary" />
              </div>
              <h3 className="text-xl font-semibold text-foreground mb-2">
                AI Recommendations
              </h3>
              <p className="text-muted-foreground">
                Get personalized movie suggestions based on your preferences, watch history, and ratings.
              </p>
            </div>

            {/* Feature 2 */}
            <div className="group p-6 rounded-xl bg-card border border-border hover:border-accent/50 transition-colors">
              <div className="p-3 bg-accent/10 rounded-lg w-fit mb-4 group-hover:bg-accent/20 transition-colors">
                <Trophy className="h-6 w-6 text-accent" />
              </div>
              <h3 className="text-xl font-semibold text-foreground mb-2">
                Earn Rewards
              </h3>
              <p className="text-muted-foreground">
                Write reviews, rate movies, and engage with the community to earn points and unlock rewards.
              </p>
            </div>

            {/* Feature 3 */}
            <div className="group p-6 rounded-xl bg-card border border-border hover:border-primary/50 transition-colors">
              <div className="p-3 bg-primary/10 rounded-lg w-fit mb-4 group-hover:bg-primary/20 transition-colors">
                <Star className="h-6 w-6 text-primary" />
              </div>
              <h3 className="text-xl font-semibold text-foreground mb-2">
                Rate & Review
              </h3>
              <p className="text-muted-foreground">
                Share your thoughts and help others discover great movies with your ratings and reviews.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Points System Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 bg-card">
        <div className="max-w-6xl mx-auto">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-3xl sm:text-4xl font-bold text-foreground mb-6">
                Earn Points, Get Rewards
              </h2>
              <p className="text-muted-foreground text-lg mb-8">
                Our gamified reward system makes discovering movies even more fun. 
                Complete activities to earn points and redeem them for exclusive rewards.
              </p>

              <div className="space-y-4">
                <div className="flex items-center gap-4 p-4 bg-background rounded-lg border border-border">
                  <div className="p-2 bg-accent/10 rounded-full">
                    <Film className="h-5 w-5 text-accent" />
                  </div>
                  <div>
                    <p className="font-semibold text-foreground">Watch Movies</p>
                    <p className="text-sm text-muted-foreground">Earn 15 points per movie</p>
                  </div>
                </div>

                <div className="flex items-center gap-4 p-4 bg-background rounded-lg border border-border">
                  <div className="p-2 bg-primary/10 rounded-full">
                    <Star className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="font-semibold text-foreground">Write Reviews</p>
                    <p className="text-sm text-muted-foreground">Earn 10 points per review</p>
                  </div>
                </div>

                <div className="flex items-center gap-4 p-4 bg-background rounded-lg border border-border">
                  <div className="p-2 bg-accent/10 rounded-full">
                    <Trophy className="h-5 w-5 text-accent" />
                  </div>
                  <div>
                    <p className="font-semibold text-foreground">First Login Bonus</p>
                    <p className="text-sm text-muted-foreground">Get 20 points instantly</p>
                  </div>
                </div>
              </div>
            </div>

            <div className="flex justify-center">
              <div className="relative">
                <div className="absolute inset-0 bg-gradient-to-r from-primary/20 to-accent/20 rounded-2xl blur-3xl" />
                <div className="relative p-8 bg-card border border-border rounded-2xl">
                  <div className="text-center">
                    <Trophy className="h-16 w-16 text-accent mx-auto mb-4" />
                    <p className="text-4xl font-bold text-foreground mb-2">10,000+</p>
                    <p className="text-muted-foreground">Points Earned by Users</p>
                  </div>
                  <div className="mt-6 pt-6 border-t border-border grid grid-cols-2 gap-4 text-center">
                    <div>
                      <p className="text-2xl font-bold text-foreground">500+</p>
                      <p className="text-sm text-muted-foreground">Movies</p>
                    </div>
                    <div>
                      <p className="text-2xl font-bold text-foreground">1000+</p>
                      <p className="text-sm text-muted-foreground">Reviews</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      {!isAuthenticated && (
        <section className="py-20 px-4 sm:px-6 lg:px-8">
          <div className="max-w-4xl mx-auto text-center">
            <h2 className="text-3xl sm:text-4xl font-bold text-foreground mb-6">
              Ready to Start Your Journey?
            </h2>
            <p className="text-muted-foreground text-lg mb-8 max-w-2xl mx-auto">
              Join thousands of movie enthusiasts and discover your next favorite film today.
            </p>
            <Link href="/register">
              <Button size="lg" className="gap-2 text-base">
                Create Free Account
                <ArrowRight className="h-5 w-5" />
              </Button>
            </Link>
          </div>
        </section>
      )}
    </div>
  );
}
