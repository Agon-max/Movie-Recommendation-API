"use client";

import Link from "next/link";
import { useAuth } from "@/contexts/auth-context";
import { Button } from "@/components/ui/button";
import {
  Film,
  Search,
  User,
  Trophy,
  LogOut,
  Settings,
  Menu,
  X,
  Sparkles,
  Gift,
} from "lucide-react";
import { useState } from "react";
import { formatPoints } from "@/lib/utils";

export function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 border-b border-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link href="/" className="flex items-center gap-2">
            <Film className="h-8 w-8 text-primary" />
            <span className="text-xl font-bold text-foreground">MovieRec</span>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center gap-6">
            <Link
              href="/movies"
              className="text-muted-foreground hover:text-foreground transition-colors"
            >
              Browse
            </Link>
            <Link
              href="/recommendations"
              className="text-muted-foreground hover:text-foreground transition-colors flex items-center gap-1"
            >
              <Sparkles className="h-4 w-4" />
              AI Picks
            </Link>
            <Link
              href="/rewards"
              className="text-muted-foreground hover:text-foreground transition-colors flex items-center gap-1"
            >
              <Gift className="h-4 w-4" />
              Rewards
            </Link>
            <Link
              href="/leaderboard"
              className="text-muted-foreground hover:text-foreground transition-colors flex items-center gap-1"
            >
              <Trophy className="h-4 w-4" />
              Leaderboard
            </Link>
            <Link
              href="/movies?search=true"
              className="text-muted-foreground hover:text-foreground transition-colors flex items-center gap-1"
            >
              <Search className="h-4 w-4" />
              Search
            </Link>
          </div>

          {/* User Section */}
          <div className="hidden md:flex items-center gap-3">
            {isAuthenticated && user ? (
              <>
                <Link
                  href="/rewards"
                  className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-accent/10 hover:bg-accent/20 transition-colors"
                  title="Open rewards"
                >
                  <Trophy className="h-4 w-4 text-accent" />
                  <span className="text-sm font-semibold text-accent">
                    {formatPoints(user.totalPoints)} pts
                  </span>
                </Link>
                <Link href="/profile">
                  <Button variant="ghost" size="sm" className="gap-2">
                    <User className="h-4 w-4" />
                    {user.username}
                  </Button>
                </Link>
                {user.role === "ADMIN" && (
                  <Link href="/admin">
                    <Button variant="outline" size="sm" className="gap-2">
                      <Settings className="h-4 w-4" />
                      Admin
                    </Button>
                  </Link>
                )}
                <Button variant="ghost" size="sm" onClick={logout} aria-label="Sign out">
                  <LogOut className="h-4 w-4" />
                </Button>
              </>
            ) : (
              <>
                <Link href="/login">
                  <Button variant="ghost" size="sm">
                    Sign In
                  </Button>
                </Link>
                <Link href="/register">
                  <Button size="sm">Get Started</Button>
                </Link>
              </>
            )}
          </div>

          {/* Mobile menu button */}
          <button
            className="md:hidden p-2"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            aria-label={mobileMenuOpen ? "Close menu" : "Open menu"}
          >
            {mobileMenuOpen ? (
              <X className="h-6 w-6" />
            ) : (
              <Menu className="h-6 w-6" />
            )}
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t border-border bg-background">
          <div className="px-4 py-4 space-y-1.5">
            <MobileLink href="/movies" onClick={() => setMobileMenuOpen(false)}>
              Browse Movies
            </MobileLink>
            <MobileLink href="/recommendations" onClick={() => setMobileMenuOpen(false)}>
              <Sparkles className="h-4 w-4 mr-2 inline" />
              AI Picks
            </MobileLink>
            <MobileLink href="/rewards" onClick={() => setMobileMenuOpen(false)}>
              <Gift className="h-4 w-4 mr-2 inline" />
              Rewards
            </MobileLink>
            <MobileLink href="/leaderboard" onClick={() => setMobileMenuOpen(false)}>
              <Trophy className="h-4 w-4 mr-2 inline" />
              Leaderboard
            </MobileLink>
            <MobileLink href="/movies?search=true" onClick={() => setMobileMenuOpen(false)}>
              <Search className="h-4 w-4 mr-2 inline" />
              Search
            </MobileLink>
            {isAuthenticated && user ? (
              <>
                <div className="flex items-center gap-2 px-3 py-2 mt-2 border-t border-border pt-3">
                  <Trophy className="h-4 w-4 text-accent" />
                  <span className="text-sm font-semibold text-accent">
                    {formatPoints(user.totalPoints)} points
                  </span>
                </div>
                <MobileLink href="/profile" onClick={() => setMobileMenuOpen(false)}>
                  Profile
                </MobileLink>
                <MobileLink href="/watch-history" onClick={() => setMobileMenuOpen(false)}>
                  Watch History
                </MobileLink>
                <MobileLink href="/survey" onClick={() => setMobileMenuOpen(false)}>
                  Taste Profile
                </MobileLink>
                {user.role === "ADMIN" && (
                  <MobileLink href="/admin" onClick={() => setMobileMenuOpen(false)}>
                    Admin Dashboard
                  </MobileLink>
                )}
                <button
                  className="block w-full text-left px-3 py-2 text-destructive hover:bg-secondary rounded-md"
                  onClick={() => {
                    logout();
                    setMobileMenuOpen(false);
                  }}
                >
                  Sign Out
                </button>
              </>
            ) : (
              <>
                <MobileLink href="/login" onClick={() => setMobileMenuOpen(false)}>
                  Sign In
                </MobileLink>
                <Link
                  href="/register"
                  className="block px-3 py-2 bg-primary text-primary-foreground rounded-md text-center font-medium"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Get Started
                </Link>
              </>
            )}
          </div>
        </div>
      )}
    </nav>
  );
}

function MobileLink({
  href,
  children,
  onClick,
}: {
  href: string;
  children: React.ReactNode;
  onClick: () => void;
}) {
  return (
    <Link
      href={href}
      onClick={onClick}
      className="block px-3 py-2 text-foreground hover:bg-secondary rounded-md transition-colors"
    >
      {children}
    </Link>
  );
}
