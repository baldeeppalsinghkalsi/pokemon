import { useState } from 'react'
import type { FormEvent } from 'react'
import './App.css'

type Pokemon = {
  id: number
  name: string
  heightDecimetres: number
  weightHectograms: number
  types: string[]
  abilities: string[]
  baseStats: {
    hp: number
    attack: number
    defense: number
    'special-attack': number
    'special-defense': number
    speed: number
  }
  spriteUrl: string
}

function App() {
  const [name, setName] = useState('pikachu')
  const [pokemon, setPokemon] = useState<Pokemon | null>(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const trimmedName = name.trim()
    if (!trimmedName) {
      setError('Please enter a Pokémon name.')
      setPokemon(null)
      return
    }

    setLoading(true)
    setError('')

    try {
      const apiBase = import.meta.env.VITE_API_URL || (import.meta.env.DEV ? 'http://localhost:8080' : '')
      const response = await fetch(`${apiBase}/api/pokemon/${encodeURIComponent(trimmedName)}`)

      if (!response.ok) {
        throw new Error('Pokémon not found')
      }

      const contentType = response.headers.get('content-type') || ''
      if (!contentType.includes('application/json')) {
        throw new Error('The API returned an unexpected response.')
      }

      const data = (await response.json()) as Pokemon
      setPokemon(data)
    } catch (err) {
      setPokemon(null)
      setError(err instanceof Error ? err.message : 'Something went wrong')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="app-shell">
      <section className="card">
        <h1>Pokédex</h1>
        <p>Enter a Pokémon name to fetch details from the backend API.</p>

        <form onSubmit={handleSubmit} className="search-form">
          <input
            value={name}
            onChange={(event) => setName(event.target.value)}
            placeholder="e.g. pikachu"
            aria-label="Pokémon name"
          />
          <button type="submit" disabled={loading}>
            {loading ? 'Searching...' : 'Search'}
          </button>
        </form>

        {error ? <p className="error">{error}</p> : null}

        {pokemon ? (
          <article className="pokemon-card">
            <div className="pokemon-header">
              <div>
                <h2>{pokemon.name}</h2>
                <p>#{pokemon.id}</p>
              </div>
              <img src={pokemon.spriteUrl} alt={pokemon.name} />
            </div>

            <div className="pokemon-details">
              <div>
                <h3>Types</h3>
                <p>{pokemon.types.join(', ')}</p>
              </div>
              <div>
                <h3>Abilities</h3>
                <p>{pokemon.abilities.join(', ')}</p>
              </div>
              <div>
                <h3>Height</h3>
                <p>{pokemon.heightDecimetres} dm</p>
              </div>
              <div>
                <h3>Weight</h3>
                <p>{pokemon.weightHectograms} hg</p>
              </div>
            </div>

            <div className="stats">
              <h3>Base Stats</h3>
              <ul>
                <li>HP: {pokemon.baseStats.hp}</li>
                <li>Attack: {pokemon.baseStats.attack}</li>
                <li>Defense: {pokemon.baseStats.defense}</li>
                <li>Special Attack: {pokemon.baseStats['special-attack']}</li>
                <li>Special Defense: {pokemon.baseStats['special-defense']}</li>
                <li>Speed: {pokemon.baseStats.speed}</li>
              </ul>
            </div>
          </article>
        ) : null}
      </section>
    </main>
  )
}

export default App
