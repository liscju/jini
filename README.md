# **JINI**

**jini** is a tool for manipulating configuration in **INI** files.

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Requirements

* **Java** 1.8 or later
* **Apache Maven** 3.5.3 or later

## Installation

First fork the project:

```bash
$ git clone https://github.com/liscju/jini.git
```

Change directory to project:

```bash
$ cd jini
```

Next build and package the project:

```bash
$ mvn package
```

Copy the archived project (zip or tar.gz) to destination directory:

```bash
$ cp jini-cli/target/jini-cli-1.0.0.zip destination-path
```

Change directory to destination path and unpack the archive:

```bash
$ cd destination-path
$ unzip jini-cli-1.0.0.zip
$ rm jini-cli-1.0.0.zip
```

Similar instructions applies to tar.gz:

```bash
$ cd destination-path
$ tar xvzf jini-cli-1.0.0.tar.gz
$ rm jini-cli-1.0.0.zip
```

Add directory to _PATH_, in case of _Windows_ use *Edit the system environment
variables*, in _UNIX-like_ operating system add destination path to _PATH_.
Check whether **jini** is installed:

```bash
$ jini --version
```

## Usage

Show version:

```bash
$ jini --version
jini 1.0.0
```

Show help:

```bash
$ jini --help
Usage: jini [-chV] [-o=FILE] [[-f=PATH] [-p=ASSIGNMENT] [-d=PATH]] [-l | -s |
            -g=PATH] [FILE...]
jini - tool for manipulating configuration in INI files
      [FILE...]            INI configuration FILE(s).
  -c, --create             Create new configuration.
  -d, --delete=PATH        Delete properties matching PATH(s).
  -f, --filter=PATH        Filter properties matching PATH(s).
  -g, --get=PATH           Write properties matching PATH(s).
  -h, --help               Show this help message and exit.
  -l, --list               Write list of properties.
  -o, --output-file=FILE   Write output to FILE.
  -p, --put=ASSIGNMENT     Put properties matching ASSIGNMENT(s).
  -s, --sections           Write section names.
  -V, --version            Print version information and exit.
```

Create **INI** file:

```bash
$ cat << EOF > example.ini
> ; last modified 1 April 2001 by John Doe
>
> modified = 1-April-2001
> author = Mary Jane
> street =
>
> [owner] ; owner properties
> name = John Doe
> organization = Acme Widgets Inc.
> boss
>
> [database]
> ; use IP address in case network name resolution is not working
> author = John Stark
> server = 192.0.2.62
> port = 143 ; port chosen by random
> file = "payroll.dat"
>
> [database.storage]
> mount = /home/ore/fs
> server = 192.0.5.132
> EOF
```

Print **INI** file in normalized form from file:

```bash
$ jini example.ini
author=Mary Jane
street=
modified=1-April-2001
[owner]
boss=
organization=Acme Widgets Inc.
name=John Doe
[database]
server=192.0.2.62
file="payroll.dat"
port=143
author=John Stark
[database.storage]
server=192.0.5.132
mount=/home/ore/fs
```

Print **INI** in normalized form from standard input:

```bash
$ cat example.ini | jini
author=Mary Jane
street=
modified=1-April-2001
[owner]
boss=
organization=Acme Widgets Inc.
name=John Doe
[database]
server=192.0.2.62
file="payroll.dat"
port=143
author=John Stark
[database.storage]
server=192.0.5.132
mount=/home/ore/fs
```

Get single property from outside (default) section:

```bash
$ jini -g author example.ini
Mary Jane
```

Get single property value from a section:

```bash
$ jini -g owner.name example.ini
John Doe
```

Get multiple properties values:

```bash
$ jini -g author,owner.name,database.server example.ini
author=Mary Jane
owner.name=John Doe
database.server=192.0.2.62
```

Get properties from section:

```bash
$ jini -g owner example.ini
owner.boss=
owner.organization=Acme Widgets Inc.
owner.name=John Doe
```

Filter file for properties and section:

```bash
$ jini -f modified,author,street,owner example.ini
author=Mary Jane
street=
modified=1-April-2001
[owner]
boss=
organization=Acme Widgets Inc.
name=John Doe
```

Delete property and sections:

```bash
$ jini -d street,database,database.storage example.ini
author=Mary Jane
modified=1-April-2001
[owner]
boss=
organization=Acme Widgets Inc.
name=John Doe
```

Put properties with section:

```bash
$ jini -p street=Washers,os.name=linux,os.version=10.0 example.ini
author=Mary Jane
street=Washers
modified=1-April-2001
[owner]
boss=
organization=Acme Widgets Inc.
name=John Doe
[database]
server=192.0.2.62
file="payroll.dat"
port=143
author=John Stark
[os]
name=linux
version=10.0
[database.storage]
server=192.0.5.132
mount=/home/ore/fs
```

List sections:

```bash
$ jini -s example.ini
.
owner
database
database.storage
```

List properties:

```bash
$ jini -l example.ini
author
street
modified
owner.boss
owner.organization
owner.name
database.server
database.file
database.port
database.author
database.storage.server
database.storage.mount
```

Write output to file:

```bash
$ jini -f modified,author,street,owner example.ini -o result.ini
$ cat result.ini
author=Mary Jane
street=
modified=1-April-2001
[owner]
boss=
organization=Acme Widgets Inc.
name=John Doe
```

Get properties that property path matches path with wildcard * (matches 
any characters except for dots):

```bash
$ jini -g '*author' example.ini
author=Mary Jane
$ jini -g 'mod*' example.ini
modified=1-April-2001
```

Get properties that property path matches path with wildcard ** (matches
any characters including dots):

```bash
$ jini -g '**author' example.ini
database.author=John Stark
author=Mary Jane
```

Join multiple **INI** files:

```bash
$ cat << EOF > example2.ini
> ; last modified 1 April 2005 by John Bro
>
> author = Joe Bro
>
> [owner] ; owner properties
> name = Joe Bro
>
> [root]
> name = PILI
> pass = NO
> EOF
```
```bash
$ jini example.ini example2.ini
author=Joe Bro
street=
modified=1-April-2001
[owner]
boss=
organization=Acme Widgets Inc.
name=Joe Bro
[database]
server=192.0.2.62
file="payroll.dat"
port=143
author=John Stark
[database.storage]
server=192.0.5.132
mount=/home/ore/fs
[root]
pass=NO
name=PILI
```

Create **INI** file with properties:

```bash
$ jini -c -p 'modified=1-April-2001,author=Mary Jane,owner.name=John Doe,owner.boss'
author=Mary Jane
modified=1-April-2001
[owner]
boss=
name=John Doe
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to
discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)




