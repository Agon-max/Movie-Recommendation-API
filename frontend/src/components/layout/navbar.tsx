"use client";

import Link from "next/link";
import { useAuth } from "@/contexts/auth-context";
import { Button } from "@/components/ui/button";
import { Film, Search, User, Trophy, LogOut, Settings, Menu, X } from "lucide-react";
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
              href="/movies?search=true"
              className="text-muted-foreground hover:text-foreground transition-colors flex items-center gap-1"
            >
              <Search className="h-4 w-4" />
              Search
            </Link>
            <Link
              href="/leaderboard"
              className="text-muted-foreground hover:text-foreground transition-colors flex items-center gap-1"
            >
              <Trophy className="h-4 w-4" />
              Leaderboard
            </Link>
          </div>

          {/* User Section */}
          <div className="hidden md:flex items-center gap-4">
            {isAuthenticated && user ? (
              <>
                <div className="flex items-center gap-2 px-3 py-1.5 bg-accent/10 rounded-full">
                  <Trophy className="h-4 w-4 text-accent" />
                  <span className="text-sm font-semibold text-accent">
                    {formatPoints(user.totalPoints)} pts
                  </span>
                </div>
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
                <Button variant="ghost" size="sm" onClick={logout}>
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
          <div className="px-4 py-4 space-y-3">
            <Link
              href="/movies"
              className="block px-3 py-2 text-foreground hover:bg-secondary rounded-md"
              onClick={() => setMobileMenuOpen(false)}
            >
              Browse Movies
            </Link>
            <Link
              href="/movies?search=true"
              className="block px-3 py-2 text-foreground hover:bg-secondary rounded-md"
              onClick={() => setMobileMenuOpen(false)}
            >
              Search
            </Link>
            <Link
              href="/leaderboard"
              className="block px-3 py-2 text-foreground hover:bg-secondary rounded-md"
              onClick={() => setMobileMenuOpen(false)}
            >
              Leaderboard
            </Link>
            {isAuthenticated && user ? (
              <>
                <div className="flex items-center gap-2 px-3 py-2">
                  <Trophy className="h-4 w-4 text-accent" />
                  <span className="text-sm font-semibold text-accent">
                    {formatPoints(user.totalPoints)} points
                  </span>
                </div>
                <Link
                  href="/profile"
                  className="block px-3 py-2 text-foreground hover:bg-secondary rounded-md"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Profile
                </Link>
                {user.role === "ADMIN" && (
                  <Link
                    href="/admin"
                    className="block px-3 py-2 text-foreground hover:bg-secondary rounded-md"
                    onClick={() => setMobileMenuOpen(false)}
                  >
                    Admin Dashboard
                  </Link>
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
                <Link
                  href="/login"
                  className="block px-3 py-2 text-foreground hover:bg-secondary rounded-md"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Sign In
                </Link>
                <Link
                  href="/register"
                  className="block px-3 py-2 bg-primary text-primary-foreground rounded-md text-center"
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
