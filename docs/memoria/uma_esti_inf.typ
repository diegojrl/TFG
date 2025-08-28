#let cover_page = (
  cover,
  degree,
  title,
  title_en,
  author,
  tutors,
  department,
  date,
) => page(
  fill: cover.back,
  header: none,
  footer: none,
  margin: (x: 3cm, top: 1cm, bottom: 4cm),
  [
    #place(
      top + center,
      dy: -0.5cm,
      dx: -1em,
      scope: "parent",
      float: true,
      clearance: 0pt,
      block(
        width: 120%,
        grid(
          align: horizon,
          columns: (2fr, 1fr, 3fr),
          cover.logo.uma, [], cover.logo.etsii,
        ),
      ),
    )

    #set align(center)
    #set text(
      size: 14pt,
      fallback: true,
      fill: cover.color,
      font: cover.font,
    )
    #upper("Escuela Técnica Superior de Ingeniería Informática")

    #upper(degree)
    #v(1fr)
    *#upper(title)*

    \

    *#upper(title_en)*
    #v(1fr)
    Realizado por\
    *#author*

    Tutorizado por\
    *#tutors*

    Departamento\
    *#department*

    #v(3cm)
    #upper("Universidad de Málaga")\
    #upper("Málaga, ") #date
  ],
)

#let back_cover = page(
  header: none,
  footer: none,
  margin: 0pt,
  [
    #place(
      bottom + center,
      block(
        fill: rgb(0, 46, 93),
        grid(
          align: horizon,
          columns: (2fr, 1fr),
          rows: (4.5cm, 0.5cm),
          grid(
            rows: (4cm, 0cm),
            image("imagenes/logos-uma/logo+uma1.png"),
            text(fill: white)[E.T.S DE INGENIERÍA INFORMÁTICA],
          ),
          align(left, text(fill: white)[\
            E.T.S de Ingeniería Informática\
            Bulevar Louis Pasteur, 35\
            Campus de Teatinos\
            29071 Málaga]),
        ),
      ),
    )
  ],
)

#let numberingH(c) = {
  if c.numbering == none {
    return []
  }
  return numbering(c.numbering, ..counter(heading).at(c.location()))
}

#let currentH() = {
  let elems = query(selector(heading.where(level: 1)).after(here()))

  if elems.len() != 0 and elems.first().location().page() == here().page() {
    return [#numberingH(elems.first()) #elems.first().body]
  } else {
    elems = query(selector(heading.where(level: 1)).before(here()))
    if elems.len() != 0 {
      return [#numberingH(elems.last()) #elems.last().body]
    }
  }
  return ""
}

#let showHeader() = {
  let elems = query(selector(heading.where(level: 1)).after(here()))

  if elems.len() != 0 and elems.first().location().page() == here().page() {
    return false
  } else {
    return true
  }
}

// Template
#let memoria = (
  body,
  degree: "<titulación>",
  title: "<título en español>",
  title_en: "<título en inglés>",
  author: "<autor>",
  tutors: "<tutores>",
  department: "<departamento>",
  abstract: none,
  abstract_en: none,
  keywords: (),
  keywords_en: (),
  date: "<fecha>",
) => {
  // Cover style
  let light_cover_params = (
    back: white,
    color: black,
    font: "Libertinus Serif",
    logo: (
      uma: image("imagenes/logos-uma/logo.png"),
      etsii: image("imagenes/logos-uma/informatica.png"),
    ),
  )

  // Meta-data
  set document(
    title: title,
    author: author,
    keywords: keywords,
  )
  set text(lang: "es")


  cover_page(
    light_cover_params,
    degree,
    title,
    title_en,
    author,
    tutors,
    department,
    date,
  )

  // Layout
  set page(
    paper: "a4",
    margin: 2.5cm,
    number-align: center,
    numbering: "I",
  )
  // Code listings styling
  show raw: it => {
    show table.cell: it => {
      if it.x == 0 {
        text(fill: gray, it)
      } else {
        it
      }
    }
    table(
      align: (right, left),
      stroke: (x, y) => {
        if x == 1 {
          (x: gray)
          if y == 0 {
            (top: gray)
          } else if y == it.lines.len() - 1 {
            (bottom: gray)
          }
        }
      },
      columns: (auto, 1fr),
      ..for line in it.lines {
        (
          str(line.number),
          line.body,
        )
      }
    )
  }

  // Body configuration
  set par(leading: 0.75em, justify: true, first-line-indent: (amount: 2em, all: true))

  set text(font: "Arial", size: 12pt, lang: "es")

  set heading(numbering: "1.")

  show table: set text(number-width: "tabular")

  show heading: set block(above: auto, below: auto)

  show heading.where(level: 2): set text(weight: "semibold")
  show heading.where(level: 3): set text(weight: "semibold", style: "italic")
  show heading.where(level: 4): set text(weight: "medium")


  // Abstract, index, body and references
  page(header: none, [], numbering: none)
  counter(page).update(1)
  if abstract != none {
    page(
      header: none,
      [
        #align(center)[
          #set text(size: 15pt)
          *_Resumen_*
        ]
        #abstract

        #if keywords.len() > 0 [
          *Palabras clave*: #keywords.join(", ")
        ]

      ],
    )
  }

  if abstract_en != none {
    page(
      header: none,
      [
        #align(center)[
          #set text(size: 15pt)
          *_Abstract_*
        ]
        #abstract_en

        #if keywords_en.len() > 0 [
          *Keywords*: #keywords_en.join(", ")
        ]
      ],
    )
  }

  counter(page).update(1)
  //Indice
  outline(target: heading.where(numbering: "1."), depth: 3)
  //Apendice
  outline(target: heading.where(numbering: "A.1."), title: [Anexo], depth: 1)

  // Cada apartado aparece en una página nueva e impar
  show heading.where(level: 1): it => pagebreak(weak: true, to: "odd") + it
  //show heading.where(level: 1): it => pagebreak(to: "odd", weak: true) + it

  set page(
    numbering: "1",
    header: context {
      if showHeader() {
        if calc.rem(here().page(), 2) == 0 [
          // even pages
          #align(left, currentH())
        ] else [
          //odd pages
          #align(right, currentH())
        ]
        line(length: 100%, stroke: 0.5pt)
      }
    },
  )

  show heading.where(level: 1): set text(weight: "bold", size: 25pt)
  show heading.where(level: 1): it => it + line(length: 100%, stroke: 0.5pt)

  counter(page).update(1)
  body

  pagebreak(weak: true, to: "even")
  back_cover
}

