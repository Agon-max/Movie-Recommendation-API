import Link from "next/link";
import { Film } from "lucide-react";

export function Footer() {
  return (
    <footer className="bg-card border-t border-border mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Brand */}
          <div className="col-span-1 md:col-span-2">
            <Link href="/" className="flex items-center gap-2 mb-4">
              <Film className="h-8 w-8 text-primary" />
              <span className="text-xl font-bold text-foreground">MovieRec</span>
            </Link>
            <p className="text-muted-foreground text-sm max-w-md">
              Discover your next favorite movie with personalized recommendations powered by AI.
              Earn rewards as you explore and engage with our community.
            </p>
          </div>

          {/* Navigation */}
          <div>
            <h3 className="font-semibold text-foreground mb-4">Explore</h3>
            <ul className="space-y-2">
              <li>
                <Link href="/movies" className="text-muted-foreground hover:text-foreground text-sm transition-colors">
                  Browse Movies
                </Link>
              </li>
              <li>
                <Link href="/leaderboard" className="text-muted-foreground hover:text-foreground text-sm transition-colors">
                  Leaderboard
                </Link>
              </li>
              <li>
                <Link href="/profile" className="text-muted-foreground hover:text-foreground text-sm transition-colors">
                  Your Profile
                </Link>
              </li>
            </ul>
          </div>

          {/* Account */}
          <div>
            <h3 className="font-semibold text-foreground mb-4">Account</h3>
            <ul className="space-y-2">
              <li>
                <Link href="/login" className="text-muted-foreground hover:text-foreground text-sm transition-colors">
                  Sign In
                </Link>
              </li>
              <li>
                <Link href="/register" className="text-muted-foreground hover:text-foreground text-sm transition-colors">
                  Create Account
                </Link>
              </li>
            </ul>
          </div>
        </div>

        <div className="border-t border-border mt-8 pt-8 text-center">
          <p className="text-muted-foreground text-sm">
            &copy; {new Date().getFullYear()} MovieRec. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
}
