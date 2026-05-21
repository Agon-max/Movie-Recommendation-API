"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useAuth } from "@/contexts/auth-context";
import { Button } from "@/components/ui/button";
import {
  Film,
  Star,
  Trophy,
  Sparkles,
  Play,
  ArrowRight,
  Brain,
  Compass,
  Heart,
  MessageSquare,
  Gift,
  TrendingUp,
} from "lucide-react";
import { movieService } from "@/services/movie.service";
import { TrendingStrip } from "@/components/home/trending-strip";
import { FeaturedSpotlight } from "@/components/home/featured-spotlight";
import { SurveyPromptModal } from "@/components/rewards/survey-prompt-modal";
import type { Genre, Movie } from "@/types";

export default function Home() {
  const { isAuthenticated, user } = useAuth();
  const [movies, setMovies] = useState<Movie[]>([]);
  const [genres, setGenres] = useState<Genre[]>([]);
  const [totalMovies, setTotalMovies] = useState(0);

  // Pull everything the home page needs in parallel. We over-fetch (24) so
  // the deck, the trending strip, and the featured spotlight can each pick
  // a different slice without re-querying.
  useEffect(() => {
    let cancelled = false;
    Promise.all([
      movieService.searchMovies({}, 0, 24).catch(() => null),
      movieService.getAllGenres().catch(() => [] as Genre[]),
    ]).then(([page, genreList]) => {
      if (cancelled) return;
      const list = (page?.content ?? []).filter((m) => !!m.posterPath);
      setMovies(list);
      setTotalMovies(page?.totalElements ?? list.length);
      setGenres(Array.isArray(genreList) ? genreList : []);
    });
    return () => {
      cancelled = true;
    };
  }, []);

  const trendingMovies = useMemo(() => movies.slice(0, 16), [movies]);

  // Pick a spotlight: highest-rated movie that has a backdrop image.
  const featuredMovie = useMemo(() => {
    const candidates = movies.filter((m) => !!m.backdropPath);
    if (candidates.length === 0) return null;
    return candidates.reduce(
      (best, cur) => (cur.averageRating > best.averageRating ? cur : best),
      candidates[0]
    );
  }, [movies]);

  return (
    <div className="flex flex-col">
      {/* ───────────────────────── Hero ───────────────────────── */}
      <section className="relative flex items-center justify-center overflow-hidden pt-16 sm:pt-20 pb-20 sm:pb-28">
        <div className="absolute inset-0 bg-gradient-to-b from-primary/20 via-background to-background" />
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-primary/10 via-transparent to-transparent" />

        <div className="relative z-10 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="flex justify-center mb-6">
            <div className="inline-flex items-center gap-2 px-4 py-2 bg-primary/10 rounded-full border border-primary/20">
              <Sparkles className="h-4 w-4 text-primary" />
              <span className="text-sm font-medium text-primary">
                AI-Powered Recommendations
              </span>
            </div>
          </div>

          <h1 className="text-4xl sm:text-5xl md:text-6xl font-bold text-foreground mb-6 text-balance">
            Discover Your Next
            <span className="text-primary"> Favorite Movie</span>
          </h1>

          <p className="text-lg sm:text-xl text-muted-foreground max-w-2xl mx-auto mb-8 text-pretty">
            Get personalized movie recommendations powered by AI. Rate, review,
            and earn rewards as you explore our extensive collection of films.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
            {isAuthenticated ? (
              <>
                <Link href="/recommendations">
                  <Button size="lg" className="gap-2 text-base">
                    <Sparkles className="h-5 w-5" />
                    Get AI Picks
                  </Button>
                </Link>
                <Link href="/movies">
                  <Button variant="outline" size="lg" className="gap-2 text-base">
                    <Play className="h-5 w-5" />
                    Browse Movies
                  </Button>
                </Link>
                <Link href="/rewards">
                  <Button variant="ghost" size="lg" className="gap-2 text-base">
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

      {/* ───────────────────────── Trending Strip ───────────────────────── */}
      {trendingMovies.length > 0 && (
        <section className="py-12 sm:py-16 border-t border-border/50">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mb-6 flex items-end justify-between gap-4">
            <div>
              <div className="inline-flex items-center gap-1.5 text-primary text-xs font-semibold uppercase tracking-wider mb-2">
                <TrendingUp className="h-3.5 w-3.5" />
                Right now
              </div>
              <h2 className="text-2xl sm:text-3xl font-bold text-foreground">
                Trending in the catalogue
              </h2>
            </div>
            <Link
              href="/movies"
              className="text-sm text-muted-foreground hover:text-foreground inline-flex items-center gap-1 transition-colors"
            >
              See all
              <ArrowRight className="h-4 w-4" />
            </Link>
          </div>
          <div className="max-w-7xl mx-auto px-2 sm:px-4 lg:px-6">
            <TrendingStrip movies={trendingMovies} />
          </div>
        </section>
      )}

      {/* ───────────────────────── Featured Spotlight ───────────────────────── */}
      {featuredMovie && <FeaturedSpotlight movie={featuredMovie} />}

      {/* ───────────────────────── How It Works ───────────────────────── */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 border-t border-border/50">
        <div className="max-w-6xl mx-auto">
          <div className="text-center mb-14">
            <div className="inline-flex items-center gap-1.5 text-accent text-xs font-semibold uppercase tracking-wider mb-2">
              <Compass className="h-3.5 w-3.5" />
              How it works
            </div>
            <h2 className="text-3xl sm:text-4xl font-bold text-foreground mb-3">
              From browsing to rewards in four steps
            </h2>
            <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
              Every interaction earns you something — points, sharper picks, or both.
            </p>
          </div>

          <ol className="grid sm:grid-cols-2 lg:grid-cols-4 gap-5 relative">
            {/* Connector line on desktop */}
            <div className="hidden lg:block absolute top-12 left-[12%] right-[12%] h-px bg-gradient-to-r from-transparent via-primary/40 to-transparent" />

            <StepCard
              n={1}
              icon={Brain}
              title="Tell us your taste"
              desc="Quick survey on genres, actors, and directors. The AI uses it as a head-start."
            />
            <StepCard
              n={2}
              icon={Compass}
              title="Discover &amp; watch"
              desc="Browse the catalogue, mark what you've watched, and unlock points each time."
              accent
            />
            <StepCard
              n={3}
              icon={MessageSquare}
              title="Rate &amp; review"
              desc="Share your take. Reviews count toward your points and refine the AI."
            />
            <StepCard
              n={4}
              icon={Gift}
              title="Redeem rewards"
              desc="Spend points on gift cards, discount codes, or cash payouts."
              accent
            />
          </ol>
        </div>
      </section>

      {/* ───────────────────────── Browse by Genre ───────────────────────── */}
      {genres.length > 0 && (
        <section className="py-20 px-4 sm:px-6 lg:px-8 bg-card border-t border-border/50">
          <div className="max-w-6xl mx-auto">
            <div className="flex items-end justify-between mb-8 gap-4">
              <div>
                <div className="inline-flex items-center gap-1.5 text-primary text-xs font-semibold uppercase tracking-wider mb-2">
                  <Film className="h-3.5 w-3.5" />
                  Explore
                </div>
                <h2 className="text-3xl sm:text-4xl font-bold text-foreground">
                  Browse by genre
                </h2>
              </div>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-4">
              {genres.map((g, i) => (
                <GenreTile key={g.id} name={g.name} index={i} />
              ))}
            </div>
          </div>
        </section>
      )}

      {/* ───────────────────────── Features ───────────────────────── */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 border-t border-border/50">
        <div className="max-w-6xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-3xl sm:text-4xl font-bold text-foreground mb-4">
              Why MovieRec?
            </h2>
            <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
              Experience movies like never before with our feature-rich platform
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            <FeatureCard
              icon={Sparkles}
              tint="primary"
              title="AI Recommendations"
              desc="Get personalized movie suggestions based on your preferences, watch history, and ratings."
            />
            <FeatureCard
              icon={Trophy}
              tint="accent"
              title="Earn Rewards"
              desc="Write reviews, rate movies, and engage with the community to earn points and unlock rewards."
            />
            <FeatureCard
              icon={Star}
              tint="primary"
              title="Rate & Review"
              desc="Share your thoughts and help others discover great movies with your ratings and reviews."
            />
          </div>
        </div>
      </section>

      {/* ───────────────────────── Points System ───────────────────────── */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 bg-card border-t border-border/50">
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
                <PointRow icon={Film} tint="accent" label="Watch Movies" reward="Earn 15 points per movie" />
                <PointRow icon={Star} tint="primary" label="Write Reviews" reward="Earn 10 points per review" />
                <PointRow icon={Trophy} tint="accent" label="First Login Bonus" reward="Get 20 points instantly" />
                <PointRow icon={Heart} tint="primary" label="Complete Taste Survey" reward="Better AI picks + 25 points" />
              </div>
            </div>

            <div className="flex justify-center">
              <div className="relative">
                <div className="absolute inset-0 bg-gradient-to-r from-primary/20 to-accent/20 rounded-2xl blur-3xl" />
                <div className="relative p-8 bg-card border border-border rounded-2xl">
                  <div className="text-center">
                    <Trophy className="h-16 w-16 text-accent mx-auto mb-4" />
                    <p className="text-4xl font-bold text-foreground mb-2">
                      {user?.totalPoints != null ? user.totalPoints.toLocaleString() : "10,000+"}
                    </p>
                    <p className="text-muted-foreground">
                      {user ? "Your points balance" : "Points Earned by Users"}
                    </p>
                  </div>
                  <div className="mt-6 pt-6 border-t border-border grid grid-cols-2 gap-4 text-center">
                    <div>
                      <p className="text-2xl font-bold text-foreground">
                        {totalMovies > 0 ? totalMovies.toLocaleString() : "500+"}
                      </p>
                      <p className="text-sm text-muted-foreground">Movies</p>
                    </div>
                    <div>
                      <p className="text-2xl font-bold text-foreground">
                        {genres.length > 0 ? genres.length : "19"}
                      </p>
                      <p className="text-sm text-muted-foreground">Genres</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Survey reminder modal — session-scoped dismissal so it comes back. */}
      <SurveyPromptModal />

      {/* ───────────────────────── Final CTA ───────────────────────── */}
      {!isAuthenticated && (
        <section className="py-20 px-4 sm:px-6 lg:px-8 border-t border-border/50">
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

// ─────────────────────────────────────────────────────────────────────────────
// Local section helpers — kept inline since they're home-page-only.
// ─────────────────────────────────────────────────────────────────────────────

interface StepCardProps {
  n: number;
  icon: typeof Compass;
  title: string;
  desc: string;
  accent?: boolean;
}
function StepCard({ n, icon: Icon, title, desc, accent }: StepCardProps) {
  return (
    <li
      className={`group relative rounded-xl border bg-background/60 backdrop-blur p-6 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl ${
        accent
          ? "border-accent/30 hover:border-accent/60"
          : "border-border hover:border-primary/50"
      }`}
    >
      <div
        className={`absolute -top-4 left-6 h-8 w-8 rounded-full grid place-items-center text-sm font-bold ring-2 ring-background ${
          accent ? "bg-accent text-accent-foreground" : "bg-primary text-primary-foreground"
        }`}
      >
        {n}
      </div>
      <div
        className={`p-2.5 rounded-lg w-fit mb-3 ${
          accent ? "bg-accent/15 text-accent" : "bg-primary/15 text-primary"
        }`}
      >
        <Icon className="h-5 w-5" />
      </div>
      <h3 className="font-semibold text-foreground mb-1.5">{title}</h3>
      <p className="text-sm text-muted-foreground leading-relaxed">{desc}</p>
    </li>
  );
}

interface FeatureCardProps {
  icon: typeof Sparkles;
  tint: "primary" | "accent";
  title: string;
  desc: string;
}
function FeatureCard({ icon: Icon, tint, title, desc }: FeatureCardProps) {
  const tintClass =
    tint === "primary"
      ? "bg-primary/10 text-primary group-hover:bg-primary/20"
      : "bg-accent/10 text-accent group-hover:bg-accent/20";
  const borderHover =
    tint === "primary" ? "hover:border-primary/60" : "hover:border-accent/60";
  return (
    <div
      className={`group relative p-7 rounded-xl bg-card border border-border ${borderHover} transition-all duration-300 hover:-translate-y-1 hover:shadow-2xl overflow-hidden`}
    >
      <div
        className={`absolute -top-12 -right-12 h-32 w-32 rounded-full blur-3xl opacity-0 group-hover:opacity-30 transition-opacity ${
          tint === "primary" ? "bg-primary" : "bg-accent"
        }`}
      />
      <div className={`relative p-3 rounded-lg w-fit mb-4 transition-colors ${tintClass}`}>
        <Icon className="h-6 w-6" />
      </div>
      <h3 className="relative text-xl font-semibold text-foreground mb-2">
        {title}
      </h3>
      <p className="relative text-muted-foreground">{desc}</p>
    </div>
  );
}

interface PointRowProps {
  icon: typeof Film;
  tint: "primary" | "accent";
  label: string;
  reward: string;
}
function PointRow({ icon: Icon, tint, label, reward }: PointRowProps) {
  return (
    <div className="flex items-center gap-4 p-4 bg-background rounded-lg border border-border hover:border-primary/40 transition-colors">
      <div
        className={`p-2 rounded-full ${
          tint === "primary" ? "bg-primary/10 text-primary" : "bg-accent/10 text-accent"
        }`}
      >
        <Icon className="h-5 w-5" />
      </div>
      <div>
        <p className="font-semibold text-foreground">{label}</p>
        <p className="text-sm text-muted-foreground">{reward}</p>
      </div>
    </div>
  );
}

interface GenreTileProps {
  name: string;
  index: number;
}
function GenreTile({ name, index }: GenreTileProps) {
  // Rotate a small palette of gradient combos so the tile grid stays
  // visually varied without needing a palette per genre.
  const palettes = [
    "from-rose-500/30 to-pink-500/10",
    "from-amber-500/30 to-orange-500/10",
    "from-emerald-500/30 to-teal-500/10",
    "from-sky-500/30 to-indigo-500/10",
    "from-violet-500/30 to-purple-500/10",
    "from-red-500/30 to-rose-500/10",
  ];
  const palette = palettes[index % palettes.length];

  return (
    <Link
      href={`/movies?search=true`}
      className={`group relative overflow-hidden rounded-xl border border-border bg-gradient-to-br ${palette} px-5 py-7 transition-all duration-300 hover:scale-[1.03] hover:border-primary/40 hover:shadow-xl`}
    >
      <div className="absolute -top-8 -right-8 h-24 w-24 rounded-full bg-white/5 blur-2xl group-hover:bg-white/10 transition-colors" />
      <div className="relative flex items-center justify-between">
        <div>
          <p className="text-xs uppercase tracking-wider text-muted-foreground mb-1">
            Genre
          </p>
          <p className="text-lg sm:text-xl font-bold text-foreground">{name}</p>
        </div>
        <ArrowRight className="h-5 w-5 text-muted-foreground group-hover:text-foreground group-hover:translate-x-1 transition-all" />
      </div>
    </Link>
  );
}
