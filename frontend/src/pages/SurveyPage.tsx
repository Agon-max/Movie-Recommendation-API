import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Sparkles, ChevronRight, X, Plus } from 'lucide-react';
import { recommendationsApi } from '../lib/api';
import { useAuthStore } from '../store/authStore';
import styles from './SurveyPage.module.css';

const GENRE_OPTIONS = ['Action', 'Comedy', 'Drama', 'Horror', 'Sci-Fi', 'Romance', 'Thriller', 'Animation', 'Documentary', 'Fantasy', 'Mystery', 'Adventure'];

const STEP_LABELS = ['Genres', 'Actors', 'Directors', 'Dislikes'];

export default function SurveyPage() {
  const { user } = useAuthStore();
  const navigate = useNavigate();
  const [step, setStep] = useState(0);
  const [genres, setGenres] = useState<string[]>([]);
  const [actorInput, setActorInput] = useState('');
  const [actors, setActors] = useState<string[]>([]);
  const [directorInput, setDirectorInput] = useState('');
  const [directors, setDirectors] = useState<string[]>([]);
  const [dislikes, setDislikes] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const toggleGenre = (g: string) => {
    setGenres((prev) => prev.includes(g) ? prev.filter((x) => x !== g) : [...prev, g]);
  };

  const addTag = (input: string, setter: React.Dispatch<React.SetStateAction<string[]>>, inputSetter: React.Dispatch<React.SetStateAction<string>>) => {
    const val = input.trim();
    if (val) {
      setter((prev) => [...new Set([...prev, val])]);
      inputSetter('');
    }
  };

  const removeTag = (list: string[], setList: React.Dispatch<React.SetStateAction<string[]>>, item: string) => {
    setList(list.filter((x) => x !== item));
  };

  const canNext = () => {
    if (step === 0) return genres.length > 0;
    return true;
  };

  const handleSubmit = async () => {
    if (!user) return;
    setSubmitting(true);
    setError('');
    try {
      await recommendationsApi.submitSurvey(user.id, {
        favoriteGenres: genres,
        favoriteActors: actors,
        favoriteDirectors: directors,
        dislikes,
      });
      navigate('/');
    } catch {
      setError('Failed to save survey. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const pct = ((step) / STEP_LABELS.length) * 100;

  return (
    <div className={styles.page}>
      <div className={styles.bg} />
      <div className={styles.card}>
        <div className={styles.topRow}>
          <div className={styles.logoRow}>
            <Sparkles size={18} />
            <span>CineMax</span>
          </div>
          <span className={styles.stepCount}>{step + 1} / {STEP_LABELS.length}</span>
        </div>

        <div className={styles.progressBar}>
          <div className={styles.progressFill} style={{ width: `${pct + 25}%` }} />
        </div>

        <h1 className={styles.heading}>
          {step === 0 && 'What genres do you love?'}
          {step === 1 && 'Favorite actors?'}
          {step === 2 && 'Favorite directors?'}
          {step === 3 && 'Anything you dislike?'}
        </h1>
        <p className={styles.sub}>
          {step === 0 && 'Select all that apply — we\'ll use these to personalize your feed.'}
          {step === 1 && 'Type actor names and press Enter. Skip if none come to mind.'}
          {step === 2 && 'Type director names and press Enter. Totally optional.'}
          {step === 3 && 'Let us know what to avoid — genres, themes, or specific content.'}
        </p>

        {step === 0 && (
          <div className={styles.genreGrid}>
            {GENRE_OPTIONS.map((g) => (
              <button
                key={g}
                onClick={() => toggleGenre(g)}
                className={`${styles.genreChip} ${genres.includes(g) ? styles.genreChipActive : ''}`}
              >
                {g}
              </button>
            ))}
          </div>
        )}

        {step === 1 && (
          <div className={styles.tagSection}>
            <div className={styles.tagInputRow}>
              <input
                placeholder="e.g. Leonardo DiCaprio"
                value={actorInput}
                onChange={(e) => setActorInput(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && addTag(actorInput, setActors, setActorInput)}
                className={styles.tagInput}
              />
              <button onClick={() => addTag(actorInput, setActors, setActorInput)} className={styles.addBtn}>
                <Plus size={16} />
              </button>
            </div>
            <div className={styles.tagList}>
              {actors.map((a) => (
                <span key={a} className={styles.tag}>
                  {a}
                  <button onClick={() => removeTag(actors, setActors, a)}><X size={12} /></button>
                </span>
              ))}
            </div>
          </div>
        )}

        {step === 2 && (
          <div className={styles.tagSection}>
            <div className={styles.tagInputRow}>
              <input
                placeholder="e.g. Christopher Nolan"
                value={directorInput}
                onChange={(e) => setDirectorInput(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && addTag(directorInput, setDirectors, setDirectorInput)}
                className={styles.tagInput}
              />
              <button onClick={() => addTag(directorInput, setDirectors, setDirectorInput)} className={styles.addBtn}>
                <Plus size={16} />
              </button>
            </div>
            <div className={styles.tagList}>
              {directors.map((d) => (
                <span key={d} className={styles.tag}>
                  {d}
                  <button onClick={() => removeTag(directors, setDirectors, d)}><X size={12} /></button>
                </span>
              ))}
            </div>
          </div>
        )}

        {step === 3 && (
          <textarea
            className={styles.dislikesTextarea}
            placeholder="e.g. No horror, no extreme violence, avoid animated films..."
            value={dislikes}
            onChange={(e) => setDislikes(e.target.value)}
            rows={5}
          />
        )}

        {error && <p className={styles.error}>{error}</p>}

        <div className={styles.actions}>
          {step > 0 && (
            <button onClick={() => setStep(step - 1)} className={styles.backBtn}>
              Back
            </button>
          )}
          {step < STEP_LABELS.length - 1 ? (
            <button
              onClick={() => canNext() && setStep(step + 1)}
              className={`${styles.nextBtn} ${!canNext() ? styles.nextBtnDisabled : ''}`}
            >
              Continue <ChevronRight size={16} />
            </button>
          ) : (
            <button onClick={handleSubmit} className={styles.submitBtn} disabled={submitting}>
              {submitting ? <span className={styles.spinner} /> : 'Get My Recommendations'}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
