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
        {/* Authentic Pokédex Hardware Accents */}
        <div className="pokedex-hardware">
          <div className="big-blue-lens"></div>
          <div className="mini-lights">
            <div className="light red"></div>
            <div className="light yellow"></div>
            <div className="light green"></div>
          </div>
        </div>

        <h1>Pokédex</h1>
        <p className="subtitle">Enter a name to query the mainframe database.</p>

        <form onSubmit={handleSubmit} className="search-form">
          <input
            value={name}
            onChange={(event) => setName(event.target.value)}
            placeholder="e.g. pikachu"
            aria-label="Pokémon name"
          />
          <button type="submit" disabled={loading}>
            {loading ? '...' : 'Go'}
          </button>
        </form>

        {error ? <p className="error">{error}</p> : null}

        {pokemon ? (
          <article className="pokemon-card">
            <div className="pokemon-header">
              <div>
                <h2>{pokemon.name}</h2>
                <p>Nº {String(pokemon.id).padStart(3, '0')}</p>
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
                <p>{pokemon.heightDecimetres / 10} m</p>
              </div>
              <div>
                <h3>Weight</h3>
                <p>{pokemon.weightHectograms / 10} kg</p>
              </div>
            </div>

            <div className="stats">
              <h3>Base Stats</h3>
              <ul>
                <li><span>HP:</span> <span>{pokemon.baseStats.hp}</span></li>
                <li><span>Attack:</span> <span>{pokemon.baseStats.attack}</span></li>
                <li><span>Defense:</span> <span>{pokemon.baseStats.defense}</span></li>
                <li><span>Sp. Atk:</span> <span>{pokemon.baseStats['special-attack']}</span></li>
                <li><span>Sp. Def:</span> <span>{pokemon.baseStats['special-defense']}</span></li>
                <li><span>Speed:</span> <span>{pokemon.baseStats.speed}</span></li>
              </ul>
            </div>
          </article>
        ) : null}
      </section>
    </main>
  )
}

export default App