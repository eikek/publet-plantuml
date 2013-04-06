## PlantUML Extension

This extension adds a [plantuml](http://plantuml.sourceforge.net/) macro to markdown.
It translates your plantuml diagrams to images.

### Prerequisites

PlantUML uses [Graphviz](http://www.graphviz.org/) for rendering some diagrams. Please see
plantuml's documentation site [here](http://plantuml.sourceforge.net/graphvizdot.html).

You should install graphviz on your system. For example, with Debian:

<pre>
apt-get install graphviz
</pre>

### Usage

Use plantuml diagrams in your markdown files:

@startuml::
Alice -> Bob: Authentication Request
Bob --> Alice: Authentication Response
@enduml

results in

<p>
@startuml
Alice -> Bob: Authentication Request
Bob --> Alice: Authentication Response
@enduml
</p>

Everything between the known `@startuml` / `@enduml` is handed to plantuml. The image is
stored in the local filesystem and the code is replaced by the corresponding html image tag.

The image files are reused and recreated only, if the source changes.

### Escaping

To escape from generating images from plantuml sources, use `@startuml:` (just append a
colon). In this case the source code is rendered as is -- without the colon.

You can wrap it in a `<pre/>` element easily by appending two colons -- `@startuml::`.

It also recognizes markdown code blocks. If indented with four spaces, or with three
backticks, the source is rendered as is.

### Configuration

The extension caches the created images in a temporary directory on the server. To avoid
endless growth, it is restricted by default to a maximum size of 50 MiB. You can change
this limit in the [publet.properties](../../configuration.html#Configuration_File) configuration
file:

    plantuml.maxDiskSize=200MiB

The disk size can be specified as a plain number, in which case the unit Byte is assumed. Otherwise,
one of the following units can be used: `Bytes`, `KiB`, `MiB`, and `GiB`.

If the size limit is reached, files are removed automatically.