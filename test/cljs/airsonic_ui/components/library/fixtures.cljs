(ns airsonic-ui.components.library.fixtures)

;; this is straight from the response cache, copied from app db after
;; browsing through the most recently listened to tracks for the first couple
;; of pages

(def responses
  {["getAlbumList2" {:type "recent", :size 100, :offset 0}]
   {:album
    [{:artistId "478"
      :name "The OOZ"
      :songCount 19
      :created "2018-06-02T12:06:11.000Z"
      :duration 3975
      :artist "King Krule"
      :year 2017
      :id "857"
      :coverArt "al-857"}
     {:genre "hip/electronic/jaz/Alternative Hip Hop/ambient"
      :artistId "644"
      :name "The Unseen"
      :songCount 24
      :created "2018-07-30T09:20:22.000Z"
      :duration 3795
      :artist "Quasimoto"
      :year 2000
      :id "1174"
      :coverArt "al-1174"}
     {:artistId "15"
      :name "The Starkiller"
      :songCount 3
      :created "2018-01-02T16:27:35.000Z"
      :duration 1158
      :artist "The Starkiller"
      :year 2013
      :id "29"
      :coverArt "al-29"}
     {:genre "Disco"
      :artistId "437"
      :name "Waffles 004"
      :songCount 1
      :created "2018-03-08T19:18:24.000Z"
      :duration 349
      :artist "Waffles"
      :year 2016
      :id "771"
      :coverArt "al-771"}
     {:genre "Electronic"
      :artistId "49"
      :name "Fated"
      :songCount 15
      :created "2018-03-12T08:36:57.000Z"
      :duration 2017
      :artist "Nosaj Thing"
      :year 2015
      :id "81"
      :coverArt "al-81"}
     {:genre "Electronic"
      :artistId "41"
      :name "Open Eye Signal (Remixes)"
      :songCount 1
      :created "2017-06-28T19:11:50.000Z"
      :duration 208
      :artist "Jon Hopkins"
      :year 2013
      :id "68"}
     {:genre "Soundtrack"
      :artistId "684"
      :name "Ghost in the Shell"
      :songCount 11
      :created "2018-10-20T08:35:00.000Z"
      :duration 2730
      :artist "Kenji Kawai"
      :year 1995
      :id "1263"
      :coverArt "al-1263"}
     {:artistId "31"
      :name "Drop Me A Line / Your Heart To Me"
      :songCount 2
      :created "2017-12-30T23:40:03.000Z"
      :duration 551
      :artist "Social Lovers"
      :year 2017
      :id "56"
      :coverArt "al-56"}
     {:id "84"
      :name "Unknown Album"
      :artist "Nosaj Thing"
      :artistId "49"
      :songCount 1
      :duration 202
      :created "2017-06-28T20:08:38.000Z"
      :genre "Unknown Genre"}
     {:genre "Electronic"
      :artistId "49"
      :name "Home"
      :songCount 11
      :created "2017-06-28T20:08:29.000Z"
      :duration 2196
      :artist "Nosaj Thing"
      :year 2013
      :id "82"
      :coverArt "al-82"}
     {:genre "Gothic"
      :artistId "403"
      :name "Three Imaginary Boys (Deluxe Edition)"
      :songCount 34
      :created "2017-11-06T20:37:32.000Z"
      :duration 6128
      :artist "The Cure"
      :year 2005
      :id "701"
      :coverArt "al-701"}
     {:genre "hip"
      :artistId "236"
      :name "Amygdala"
      :songCount 13
      :created "2018-08-14T20:23:42.000Z"
      :duration 4665
      :artist "DJ Koze"
      :year 2013
      :id "403"
      :coverArt "al-403"}
     {:genre "Downtempo"
      :artistId "596"
      :name "II"
      :songCount 10
      :created "2017-06-28T20:15:56.000Z"
      :duration 2755
      :artist "Raz Ohara and The Odd Orchestra"
      :year 2009
      :id "1040"}
     {:genre "Soul"
      :artistId "436"
      :name "Overgrown"
      :songCount 11
      :created "2018-01-02T08:43:32.000Z"
      :duration 2569
      :artist "James Blake"
      :year 2013
      :id "770"
      :coverArt "al-770"}
     {:genre "Electronic"
      :artistId "436"
      :name "Life Round Here (feat. Chance the Rapper) - Single"
      :songCount 1
      :created "2017-06-28T19:23:25.000Z"
      :duration 188
      :artist "James Blake"
      :year 2013
      :id "768"
      :coverArt "al-768"}
     {:genre "Hip Hop"
      :artistId "290"
      :name "C'mon! EP"
      :songCount 6
      :created "2017-06-28T19:12:11.000Z"
      :duration 1163
      :artist "Fatoni"
      :year 2015
      :id "508"
      :coverArt "al-508"}
     {:genre "electronic"
      :artistId "683"
      :name "Das Ziel ist im Weg"
      :songCount 10
      :created "2018-10-17T11:01:24.000Z"
      :duration 2130
      :artist "Mine"
      :year 2016
      :id "1262"
      :coverArt "al-1262"}
     {:genre "Downtempo"
      :artistId "479"
      :name "Days to Come"
      :songCount 18
      :created "2017-06-28T18:47:44.000Z"
      :duration 4627
      :artist "Bonobo"
      :year 2006
      :id "861"
      :coverArt "al-861"}
     {:genre "Electronic"
      :artistId "206"
      :name "Andorra"
      :songCount 9
      :created "2017-11-25T20:47:26.000Z"
      :duration 2581
      :artist "Caribou"
      :year 2007
      :id "336"
      :coverArt "al-336"}
     {:genre "Electronic"
      :artistId "206"
      :name "Melody Day"
      :songCount 3
      :created "2017-11-25T20:49:51.000Z"
      :duration 683
      :artist "Caribou"
      :year 2007
      :id "335"
      :coverArt "al-335"}
     {:id "707"
      :name "lassmalaura"
      :artist "lassmalaura"
      :artistId "406"
      :songCount 2
      :duration 8241
      :created "2017-06-28T18:27:36.000Z"}
     {:genre "Idm"
      :artistId "597"
      :name "Cerulean"
      :songCount 12
      :created "2017-06-28T18:44:43.000Z"
      :duration 2594
      :artist "Baths"
      :year 2010
      :id "1042"
      :coverArt "al-1042"}
     {:genre "Electronic"
      :artistId "64"
      :name "Plörre"
      :songCount 11
      :created "2017-06-28T19:17:41.000Z"
      :duration 2495
      :artist "Frittenbude"
      :year 2010
      :id "109"}
     {:genre "Electronic"
      :artistId "388"
      :name "Rongorongo Remixed"
      :songCount 11
      :created "2017-06-28T19:57:48.000Z"
      :duration 3590
      :artist "Me Succeeds"
      :year 2013
      :id "654"
      :coverArt "al-654"}
     {:genre "Hip-Hop"
      :artistId "270"
      :name "Über Liebe VLS"
      :songCount 1
      :created "2017-06-28T18:42:12.000Z"
      :duration 169
      :artist "Audio88 und Yassin"
      :year 2011
      :id "469"}
     {:genre "Hip-Hop"
      :artistId "523"
      :name "Über Liebe VLS"
      :songCount 1
      :created "2017-06-28T20:21:03.000Z"
      :duration 275
      :artist "Suff Daddy"
      :year 2011
      :id "940"}
     {:id "25"
      :name "Nhar, Lee Burton"
      :artist "Nhar, Lee Burton"
      :artistId "13"
      :songCount 1
      :duration 482
      :created "2017-06-28T18:30:39.000Z"}
     {:genre "Indie Dance / Nu Disco"
      :artistId "360"
      :name "Salto"
      :songCount 1
      :created "2018-01-02T18:55:06.000Z"
      :duration 414
      :artist "Martin Heimann"
      :year 2016
      :id "625"
      :coverArt "al-625"}
     {:id "273"
      :name "[via XLR8R]"
      :artist "Lianne La Havas"
      :artistId "165"
      :coverArt "al-273"
      :songCount 1
      :duration 307
      :created "2017-06-28T19:35:28.000Z"}
     {:artistId "249"
      :name "Free Downloads"
      :songCount 1
      :created "2017-06-28T19:10:19.000Z"
      :duration 286
      :artist "Emancipator feat. Sigur Rós vs. Mobb Deep"
      :year 2011
      :id "419"
      :coverArt "al-419"}
     {:genre "Electronic"
      :artistId "64"
      :name "Und täglich grüßt das Murmeltier"
      :songCount 3
      :created "2017-12-31T09:03:39.000Z"
      :duration 652
      :artist "Frittenbude"
      :year 2010
      :id "107"}
     {:genre "Electronic"
      :artistId "206"
      :name "Start Breaking My Heart"
      :songCount 20
      :created "2017-11-25T20:47:51.000Z"
      :duration 6197
      :artist "Caribou"
      :year 2006
      :id "338"
      :coverArt "al-338"}
     {:genre "Electronic"
      :artistId "206"
      :name "The Milk Of Human Kindness"
      :songCount 11
      :created "2017-11-25T20:41:58.000Z"
      :duration 2412
      :artist "Caribou"
      :year 2005
      :id "337"
      :coverArt "al-337"}
     {:genre "electronic"
      :artistId "424"
      :name "Permanent Vacation 3"
      :songCount 47
      :created "2017-06-28T20:29:36.000Z"
      :duration 18682
      :artist "Various Artists"
      :year 2014
      :id "747"
      :coverArt "al-747"}
     {:genre "Electronic"
      :artistId "162"
      :name "Music Has the Right to Children"
      :songCount 18
      :created "2017-06-28T18:46:28.000Z"
      :duration 4226
      :artist "Boards of Canada"
      :year 2004
      :id "270"
      :coverArt "al-270"}
     {:id "276"
      :name "Nostalgia 77"
      :artist "Nostalgia 77"
      :artistId "168"
      :songCount 1
      :duration 277
      :created "2017-06-28T18:31:28.000Z"}
     {:genre "Electronic"
      :artistId "597"
      :name "Obsidian"
      :songCount 10
      :created "2017-06-28T18:43:58.000Z"
      :duration 2596
      :artist "Baths"
      :year 2013
      :id "1041"
      :coverArt "al-1041"}
     {:id "954"
      :name "[via XLR8R.com]"
      :artist "Burial"
      :artistId "530"
      :coverArt "al-954"
      :songCount 1
      :duration 297
      :created "2017-06-28T18:49:04.000Z"}
     {:genre "Uk Garage"
      :artistId "530"
      :name "Kindred EP"
      :songCount 3
      :created "2017-06-28T18:49:36.000Z"
      :duration 1839
      :artist "Burial"
      :year 2012
      :id "953"
      :coverArt "al-953"}
     {:genre "Unknown"
      :artistId "430"
      :name "Rampue"
      :songCount 6
      :created "2017-06-28T18:34:00.000Z"
      :duration 16433
      :artist "Rampue"
      :year 2012
      :id "753"
      :coverArt "al-753"}
     {:artistId "96"
      :name "www.soundcloud.com/rampue"
      :songCount 1
      :created "2017-06-28T19:31:41.000Z"
      :duration 424
      :artist "Klima"
      :year 2013
      :id "166"
      :coverArt "al-166"}
     {:artistId "463"
      :name "soundcloud.com/rampue"
      :songCount 1
      :created "2017-06-28T19:35:30.000Z"
      :duration 523
      :artist "Leonard Cohen"
      :year 2014
      :id "831"
      :coverArt "al-831"}
     {:artistId "463"
      :name "The Future"
      :songCount 9
      :created "2018-01-16T11:14:41.000Z"
      :duration 3579
      :artist "Leonard Cohen"
      :year 2012
      :id "821"
      :coverArt "al-821"}
     {:id "1001"
      :name "Lonski & Classen"
      :artist "Lonski & Classen"
      :artistId "566"
      :coverArt "al-1001"
      :songCount 1
      :duration 248
      :created "2017-06-28T20:07:24.000Z"}
     {:genre "Podcast"
      :artistId "199"
      :name "Waterkant Souvenirs Podcast"
      :songCount 1
      :created "2017-06-28T20:00:25.000Z"
      :duration 5341
      :artist "Mira"
      :year 2012
      :id "325"}
     {:id "324"
      :name "Familiar Forest Festival 2012"
      :artist "Mira"
      :artistId "199"
      :songCount 1
      :duration 6695
      :created "2017-06-28T20:00:35.000Z"
      :year 2012}
     {:genre "Ambient"
      :artistId "188"
      :name "We're New Here"
      :songCount 13
      :created "2017-06-28T19:18:06.000Z"
      :duration 2135
      :artist "Gil Scott-Heron and Jamie xx"
      :year 2011
      :id "310"
      :coverArt "al-310"}
     {:genre "Gothic"
      :artistId "403"
      :name "Galore : The Singles 87 - 97"
      :songCount 18
      :created "2017-11-06T20:51:35.000Z"
      :duration 4369
      :artist "The Cure"
      :year 1997
      :id "684"
      :coverArt "al-684"}
     {:genre "IDM"
      :artistId "333"
      :name "Remixes Compiled"
      :songCount 12
      :created "2017-06-28T20:22:43.000Z"
      :duration 3233
      :artist "Telefon Tel Aviv"
      :year 2007
      :id "723"
      :coverArt "al-723"}
     {:artistId "230"
      :name "Ufordian Edits"
      :songCount 1
      :created "2018-02-19T22:55:59.000Z"
      :duration 331
      :artist "Peter Power"
      :year 2015
      :id "393"
      :coverArt "al-393"}
     {:genre "Other"
      :artistId "528"
      :name "Dream Runner EP"
      :songCount 6
      :created "2017-06-28T18:39:24.000Z"
      :duration 899
      :artist "Annu"
      :year 2009
      :id "948"}
     {:genre "Techno"
      :artistId "75"
      :name "Unknown"
      :songCount 8
      :created "2017-06-28T20:17:47.000Z"
      :duration 2841
      :artist "Saschienne"
      :year 2012
      :id "124"
      :coverArt "al-124"}
     {:genre "Nintendocore"
      :artistId "306"
      :name "Nach der Kippe Pogo!?"
      :songCount 11
      :created "2017-06-28T18:40:09.000Z"
      :duration 1508
      :artist "Antitainment"
      :year 2007
      :id "532"
      :coverArt "al-532"}
     {:genre "Electronic"
      :artistId "206"
      :name "Swim"
      :songCount 9
      :created "2017-11-25T20:06:58.000Z"
      :duration 2596
      :artist "Caribou"
      :year 2010
      :id "339"
      :coverArt "al-339"}
     {:genre "trance"
      :artistId "117"
      :name "Nymphs III"
      :songCount 2
      :created "2017-06-28T20:04:17.000Z"
      :duration 1080
      :artist "Nicolas Jaar"
      :year 2015
      :id "201"}
     {:genre "Gothic"
      :artistId "403"
      :name "Wish"
      :songCount 12
      :created "2018-01-02T14:29:04.000Z"
      :duration 3976
      :artist "The Cure"
      :year 1992
      :id "685"
      :coverArt "al-685"}
     {:genre "Gothic"
      :artistId "403"
      :name "Show (Live)"
      :songCount 18
      :created "2018-01-02T14:30:03.000Z"
      :duration 5316
      :artist "The Cure"
      :id "698"
      :coverArt "al-698"}
     {:genre "Gothic"
      :artistId "403"
      :name "Mixed Up"
      :songCount 11
      :created "2018-01-02T14:29:43.000Z"
      :duration 4260
      :artist "The Cure"
      :year 1990
      :id "692"
      :coverArt "al-692"}
     {:id "1257"
      :name "Saal"
      :artist "Serengeti"
      :artistId "678"
      :songCount 13
      :duration 2437
      :created "2018-09-20T17:02:50.000Z"
      :year 2013}
     {:genre "Hip Hop"
      :artistId "204"
      :name "Leaders Of The Brew School"
      :songCount 16
      :created "2017-06-28T18:45:16.000Z"
      :duration 2214
      :artist "Betty Ford Boys"
      :year 2013
      :id "331"}
     {:id "202"
      :name "Sirens"
      :artist "Nicolas Jaar"
      :artistId "117"
      :songCount 7
      :duration 2841
      :created "2017-06-28T20:04:34.000Z"
      :year 2016}
     {:genre "techno"
      :artistId "682"
      :name "Piñata"
      :songCount 21
      :created "2018-10-09T15:30:48.000Z"
      :duration 3963
      :artist "Freddie Gibbs & Madlib"
      :year 2014
      :id "1261"
      :coverArt "al-1261"}
     {:genre "electronic"
      :artistId "681"
      :name "We Must Become the Pitiless Censors of Ourselves"
      :songCount 11
      :created "2018-10-08T17:21:47.000Z"
      :duration 1916
      :artist "John Maus"
      :year 2011
      :id "1260"
      :coverArt "al-1260"}
     {:artistId "514"
      :name "Time"
      :songCount 1
      :created "2017-07-24T13:19:05.000Z"
      :duration 247
      :artist "Lokke"
      :year 2015
      :id "923"
      :coverArt "al-923"}
     {:genre "jazz"
      :artistId "680"
      :name "These Things Take Time"
      :songCount 13
      :created "2018-10-08T17:21:09.000Z"
      :duration 3013
      :artist "Molly Nilsson"
      :year 2008
      :id "1259"
      :coverArt "al-1259"}
     {:artistId "463"
      :name "Songs of Love and Hate"
      :songCount 4
      :created "2018-01-16T11:13:54.000Z"
      :duration 1273
      :artist "Leonard Cohen"
      :year 1970
      :id "829"
      :coverArt "al-829"}
     {:artistId "187"
      :name "Vacation EP"
      :songCount 7
      :created "2017-06-28T20:19:17.000Z"
      :duration 1902
      :artist "Shlohmo"
      :year 2012
      :id "305"
      :coverArt "al-305"}
     {:genre "Electronic"
      :artistId "187"
      :name "Vacation (Remixes)"
      :songCount 6
      :created "2017-06-28T20:19:23.000Z"
      :duration 3559
      :artist "Shlohmo"
      :year 2012
      :id "303"}
     {:genre "WeDidIt"
      :artistId "302"
      :name "Salvation Remixes"
      :songCount 3
      :created "2017-06-28T20:14:04.000Z"
      :duration 739
      :artist "Purple"
      :year 2013
      :id "525"
      :coverArt "al-525"}
     {:genre "Alternative Rock / Indie Rock"
      :artistId "16"
      :name "Sleeping With Ghosts"
      :songCount 22
      :created "2017-11-06T20:39:23.000Z"
      :duration 5232
      :artist "Placebo"
      :year 2003
      :id "38"
      :coverArt "al-38"}
     {:genre "Funk/Hip-Hop"
      :artistId "198"
      :name "Looking For the Perfect Beat"
      :songCount 13
      :created "2017-06-28T18:36:47.000Z"
      :duration 4521
      :artist "Afrika Bambaataa"
      :year 2001
      :id "323"
      :coverArt "al-323"}
     {:artistId "103"
      :name "edits & cuts"
      :songCount 14
      :created "2017-06-28T19:37:20.000Z"
      :duration 3550
      :artist "M.Rux"
      :year 2014
      :id "182"
      :coverArt "al-182"}
     {:genre "Techno"
      :artistId "117"
      :name "Marks / Angles"
      :songCount 3
      :created "2017-06-28T20:03:46.000Z"
      :duration 1000
      :artist "Nicolas Jaar"
      :year 2010
      :id "196"}
     {:genre "Electronic"
      :artistId "73"
      :name "Don't Break My Love EP"
      :songCount 2
      :created "2017-06-28T20:05:16.000Z"
      :duration 673
      :artist "Nicolas Jaar & Theatre Roosevelt"
      :year 2011
      :id "122"
      :coverArt "al-122"}
     {:genre "Electronic"
      :artistId "233"
      :name "Mother Earth's Plantasia"
      :songCount 10
      :created "2018-05-28T21:31:55.000Z"
      :duration 1837
      :artist "Mort Garson"
      :year 1976
      :id "397"
      :coverArt "al-397"}
     {:genre "Psychedelic Rock"
      :artistId "424"
      :name
      "Nuggets: Original Artyfacts From the First Psychedelic Era, 1965-1968"
      :songCount 27
      :created "2018-02-21T12:01:38.000Z"
      :duration 4614
      :artist "Various Artists"
      :year 1998
      :id "743"
      :coverArt "al-743"}
     {:genre "Psychedelic Rock"
      :artistId "37"
      :name "Phluph"
      :songCount 10
      :created "2018-03-05T16:31:46.000Z"
      :duration 2182
      :artist "Phluph"
      :year 2001
      :id "64"
      :coverArt "al-64"}
     {:genre "Rock"
      :artistId "305"
      :name "The Best of Talking Heads (Remastered)"
      :songCount 18
      :created "2018-01-22T11:00:50.000Z"
      :duration 4618
      :artist "Talking Heads"
      :year 2004
      :id "529"
      :coverArt "al-529"}
     {:genre "Electronic"
      :artistId "50"
      :name "Divide And Exit"
      :songCount 14
      :created "2018-01-21T14:47:59.000Z"
      :duration 2417
      :artist "Sleaford Mods"
      :year 2014
      :id "86"
      :coverArt "al-86"}
     {:genre "electronic"
      :artistId "349"
      :name "Fade to Grey: The Best of Visage"
      :songCount 12
      :created "2018-08-29T13:01:26.000Z"
      :duration 2757
      :artist "Visage"
      :year 1993
      :id "1234"}
     {:genre "electronic"
      :artistId "334"
      :name "Hounds of Love"
      :songCount 18
      :created "2018-08-29T13:00:32.000Z"
      :duration 4419
      :artist "Kate Bush"
      :year 1997
      :id "1215"
      :coverArt "al-1215"}
     {:genre "Psychedelic"
      :artistId "424"
      :name
      "Forge Your Own Chains: Heavy Psychedelic Ballads and Dirges 1968-1974"
      :songCount 15
      :created "2018-01-27T12:23:47.000Z"
      :duration 4241
      :artist "Various Artists"
      :id "742"
      :coverArt "al-742"}
     {:genre "Live Archive"
      :artistId "141"
      :name "2017-08-28 Rough Trade NYC, Brooklyn, NY"
      :songCount 4
      :created "2018-01-19T23:07:20.000Z"
      :duration 2483
      :artist "Sunburned Hand of the Man"
      :year 2017
      :id "242"
      :coverArt "al-242"}
     {:genre "electronic"
      :artistId "236"
      :name "Knock Knock"
      :songCount 16
      :created "2018-06-09T23:04:20.000Z"
      :duration 4710
      :artist "DJ Koze"
      :year 2018
      :id "401"
      :coverArt "al-401"}
     {:genre "Nintendocore"
      :artistId "306"
      :name "Gymnasiastik mit Antitainment"
      :songCount 6
      :created "2017-06-28T18:39:58.000Z"
      :duration 795
      :artist "Antitainment"
      :year 2004
      :id "533"}
     {:genre "Electronic"
      :artistId "584"
      :name "Amok"
      :songCount 9
      :created "2017-06-28T18:41:30.000Z"
      :duration 2681
      :artist "Atoms for Peace"
      :year 2013
      :id "1023"
      :coverArt "al-1023"}
     {:artistId "26"
      :name "TamponTango I"
      :songCount 3
      :created "2017-11-23T23:18:43.000Z"
      :duration 851
      :artist "Diederdas"
      :year 2017
      :id "51"
      :coverArt "al-51"}
     {:genre "electronic"
      :artistId "679"
      :name "Heaven and Earth"
      :songCount 16
      :created "2018-09-20T22:07:23.000Z"
      :duration 8672
      :artist "Kamasi Washington"
      :year 2018
      :id "1258"
      :coverArt "al-1258"}
     {:genre "rhy"
      :artistId "661"
      :name "Yawn Zen"
      :songCount 12
      :created "2018-08-21T21:36:43.000Z"
      :duration 1883
      :artist "Mndsgn"
      :year 2014
      :id "1200"
      :coverArt "al-1200"}
     {:genre "Rap"
      :artistId "677"
      :name "Elephant Eyelash"
      :songCount 12
      :created "2018-09-20T17:02:08.000Z"
      :duration 2478
      :artist "Why?"
      :year 2005
      :id "1256"}
     {:genre "Electronic"
      :artistId "41"
      :name "Immunity"
      :songCount 8
      :created "2017-06-28T19:28:24.000Z"
      :duration 3604
      :artist "Jon Hopkins"
      :year 2013
      :id "104"
      :coverArt "al-104"}
     {:genre "IDM / Trip-Hop / Experimental"
      :artistId "454"
      :name "New Energy"
      :songCount 14
      :created "2017-11-25T19:44:56.000Z"
      :duration 3381
      :artist "Four Tet"
      :year 2017
      :id "800"
      :coverArt "al-800"}
     {:genre "Electronic"
      :artistId "633"
      :name "ƒIN (Special Edition)"
      :songCount 20
      :created "2017-06-28T19:26:41.000Z"
      :duration 5822
      :artist "John Talabot"
      :year 2012
      :id "1159"}
     {:artistId "412"
      :name "A Moot Point"
      :songCount 2
      :created "2017-06-28T18:32:24.000Z"
      :duration 857
      :artist "Pional"
      :year 2010
      :id "719"
      :coverArt "al-719"}
     {:id "740"
      :name "KR Family EP, Pt. 1"
      :artist "Peter Power"
      :artistId "230"
      :coverArt "al-740"
      :songCount 3
      :duration 1333
      :created "2017-06-28T20:31:06.000Z"}
     {:genre "House"
      :artistId "482"
      :name "Busy Days For Fools"
      :songCount 11
      :created "2017-06-28T19:35:05.000Z"
      :duration 3238
      :artist "Lee Burton"
      :year 2012
      :id "866"
      :coverArt "al-866"}
     {:id "851"
      :name "Ry & Frank Wiedemann"
      :artist "Ry & Frank Wiedemann"
      :artistId "472"
      :songCount 1
      :duration 485
      :created "2017-06-28T18:34:23.000Z"}
     {:genre "Electronic"
      :artistId "58"
      :name "Deep Cuts"
      :songCount 17
      :created "2017-12-22T08:21:19.000Z"
      :duration 3321
      :artist "The Knife"
      :year 2003
      :id "96"
      :coverArt "al-96"}
     {:artistId "125"
      :name "VIA Remixes"
      :songCount 1
      :created "2017-06-28T18:27:59.000Z"
      :duration 362
      :artist "Andi Otto"
      :year 2017
      :id "211"
      :coverArt "al-211"}
     {:artistId "626"
      :name "Hummingbird / Milk & Honey"
      :songCount 2
      :created "2017-11-23T21:27:00.000Z"
      :duration 303
      :artist "Luca Nieri"
      :year 2016
      :id "1150"
      :coverArt "al-1150"}]}
   ["getAlbumList2" {:type "recent", :size 100, :offset 20}]
   {:album
    [{:id "707"
      :name "lassmalaura"
      :artist "lassmalaura"
      :artistId "406"
      :songCount 2
      :duration 8241
      :created "2017-06-28T18:27:36.000Z"}
     {:genre "Idm"
      :artistId "597"
      :name "Cerulean"
      :songCount 12
      :created "2017-06-28T18:44:43.000Z"
      :duration 2594
      :artist "Baths"
      :year 2010
      :id "1042"
      :coverArt "al-1042"}
     {:genre "Electronic"
      :artistId "64"
      :name "Plörre"
      :songCount 11
      :created "2017-06-28T19:17:41.000Z"
      :duration 2495
      :artist "Frittenbude"
      :year 2010
      :id "109"}
     {:genre "Electronic"
      :artistId "388"
      :name "Rongorongo Remixed"
      :songCount 11
      :created "2017-06-28T19:57:48.000Z"
      :duration 3590
      :artist "Me Succeeds"
      :year 2013
      :id "654"
      :coverArt "al-654"}
     {:genre "Hip-Hop"
      :artistId "270"
      :name "Über Liebe VLS"
      :songCount 1
      :created "2017-06-28T18:42:12.000Z"
      :duration 169
      :artist "Audio88 und Yassin"
      :year 2011
      :id "469"}
     {:genre "Hip-Hop"
      :artistId "523"
      :name "Über Liebe VLS"
      :songCount 1
      :created "2017-06-28T20:21:03.000Z"
      :duration 275
      :artist "Suff Daddy"
      :year 2011
      :id "940"}
     {:id "25"
      :name "Nhar, Lee Burton"
      :artist "Nhar, Lee Burton"
      :artistId "13"
      :songCount 1
      :duration 482
      :created "2017-06-28T18:30:39.000Z"}
     {:genre "Indie Dance / Nu Disco"
      :artistId "360"
      :name "Salto"
      :songCount 1
      :created "2018-01-02T18:55:06.000Z"
      :duration 414
      :artist "Martin Heimann"
      :year 2016
      :id "625"
      :coverArt "al-625"}
     {:id "273"
      :name "[via XLR8R]"
      :artist "Lianne La Havas"
      :artistId "165"
      :coverArt "al-273"
      :songCount 1
      :duration 307
      :created "2017-06-28T19:35:28.000Z"}
     {:artistId "249"
      :name "Free Downloads"
      :songCount 1
      :created "2017-06-28T19:10:19.000Z"
      :duration 286
      :artist "Emancipator feat. Sigur Rós vs. Mobb Deep"
      :year 2011
      :id "419"
      :coverArt "al-419"}
     {:genre "Electronic"
      :artistId "64"
      :name "Und täglich grüßt das Murmeltier"
      :songCount 3
      :created "2017-12-31T09:03:39.000Z"
      :duration 652
      :artist "Frittenbude"
      :year 2010
      :id "107"}
     {:genre "Electronic"
      :artistId "206"
      :name "Start Breaking My Heart"
      :songCount 20
      :created "2017-11-25T20:47:51.000Z"
      :duration 6197
      :artist "Caribou"
      :year 2006
      :id "338"
      :coverArt "al-338"}
     {:genre "Electronic"
      :artistId "206"
      :name "The Milk Of Human Kindness"
      :songCount 11
      :created "2017-11-25T20:41:58.000Z"
      :duration 2412
      :artist "Caribou"
      :year 2005
      :id "337"
      :coverArt "al-337"}
     {:genre "electronic"
      :artistId "424"
      :name "Permanent Vacation 3"
      :songCount 47
      :created "2017-06-28T20:29:36.000Z"
      :duration 18682
      :artist "Various Artists"
      :year 2014
      :id "747"
      :coverArt "al-747"}
     {:genre "Electronic"
      :artistId "162"
      :name "Music Has the Right to Children"
      :songCount 18
      :created "2017-06-28T18:46:28.000Z"
      :duration 4226
      :artist "Boards of Canada"
      :year 2004
      :id "270"
      :coverArt "al-270"}
     {:id "276"
      :name "Nostalgia 77"
      :artist "Nostalgia 77"
      :artistId "168"
      :songCount 1
      :duration 277
      :created "2017-06-28T18:31:28.000Z"}
     {:genre "Electronic"
      :artistId "597"
      :name "Obsidian"
      :songCount 10
      :created "2017-06-28T18:43:58.000Z"
      :duration 2596
      :artist "Baths"
      :year 2013
      :id "1041"
      :coverArt "al-1041"}
     {:id "954"
      :name "[via XLR8R.com]"
      :artist "Burial"
      :artistId "530"
      :coverArt "al-954"
      :songCount 1
      :duration 297
      :created "2017-06-28T18:49:04.000Z"}
     {:genre "Uk Garage"
      :artistId "530"
      :name "Kindred EP"
      :songCount 3
      :created "2017-06-28T18:49:36.000Z"
      :duration 1839
      :artist "Burial"
      :year 2012
      :id "953"
      :coverArt "al-953"}
     {:genre "Unknown"
      :artistId "430"
      :name "Rampue"
      :songCount 6
      :created "2017-06-28T18:34:00.000Z"
      :duration 16433
      :artist "Rampue"
      :year 2012
      :id "753"
      :coverArt "al-753"}
     {:artistId "96"
      :name "www.soundcloud.com/rampue"
      :songCount 1
      :created "2017-06-28T19:31:41.000Z"
      :duration 424
      :artist "Klima"
      :year 2013
      :id "166"
      :coverArt "al-166"}
     {:artistId "463"
      :name "soundcloud.com/rampue"
      :songCount 1
      :created "2017-06-28T19:35:30.000Z"
      :duration 523
      :artist "Leonard Cohen"
      :year 2014
      :id "831"
      :coverArt "al-831"}
     {:artistId "463"
      :name "The Future"
      :songCount 9
      :created "2018-01-16T11:14:41.000Z"
      :duration 3579
      :artist "Leonard Cohen"
      :year 2012
      :id "821"
      :coverArt "al-821"}
     {:id "1001"
      :name "Lonski & Classen"
      :artist "Lonski & Classen"
      :artistId "566"
      :coverArt "al-1001"
      :songCount 1
      :duration 248
      :created "2017-06-28T20:07:24.000Z"}
     {:genre "Podcast"
      :artistId "199"
      :name "Waterkant Souvenirs Podcast"
      :songCount 1
      :created "2017-06-28T20:00:25.000Z"
      :duration 5341
      :artist "Mira"
      :year 2012
      :id "325"}
     {:id "324"
      :name "Familiar Forest Festival 2012"
      :artist "Mira"
      :artistId "199"
      :songCount 1
      :duration 6695
      :created "2017-06-28T20:00:35.000Z"
      :year 2012}
     {:genre "Ambient"
      :artistId "188"
      :name "We're New Here"
      :songCount 13
      :created "2017-06-28T19:18:06.000Z"
      :duration 2135
      :artist "Gil Scott-Heron and Jamie xx"
      :year 2011
      :id "310"
      :coverArt "al-310"}
     {:genre "Gothic"
      :artistId "403"
      :name "Galore : The Singles 87 - 97"
      :songCount 18
      :created "2017-11-06T20:51:35.000Z"
      :duration 4369
      :artist "The Cure"
      :year 1997
      :id "684"
      :coverArt "al-684"}
     {:genre "IDM"
      :artistId "333"
      :name "Remixes Compiled"
      :songCount 12
      :created "2017-06-28T20:22:43.000Z"
      :duration 3233
      :artist "Telefon Tel Aviv"
      :year 2007
      :id "723"
      :coverArt "al-723"}
     {:artistId "230"
      :name "Ufordian Edits"
      :songCount 1
      :created "2018-02-19T22:55:59.000Z"
      :duration 331
      :artist "Peter Power"
      :year 2015
      :id "393"
      :coverArt "al-393"}
     {:genre "Other"
      :artistId "528"
      :name "Dream Runner EP"
      :songCount 6
      :created "2017-06-28T18:39:24.000Z"
      :duration 899
      :artist "Annu"
      :year 2009
      :id "948"}
     {:genre "Techno"
      :artistId "75"
      :name "Unknown"
      :songCount 8
      :created "2017-06-28T20:17:47.000Z"
      :duration 2841
      :artist "Saschienne"
      :year 2012
      :id "124"
      :coverArt "al-124"}
     {:genre "Nintendocore"
      :artistId "306"
      :name "Nach der Kippe Pogo!?"
      :songCount 11
      :created "2017-06-28T18:40:09.000Z"
      :duration 1508
      :artist "Antitainment"
      :year 2007
      :id "532"
      :coverArt "al-532"}
     {:genre "Electronic"
      :artistId "206"
      :name "Swim"
      :songCount 9
      :created "2017-11-25T20:06:58.000Z"
      :duration 2596
      :artist "Caribou"
      :year 2010
      :id "339"
      :coverArt "al-339"}
     {:genre "trance"
      :artistId "117"
      :name "Nymphs III"
      :songCount 2
      :created "2017-06-28T20:04:17.000Z"
      :duration 1080
      :artist "Nicolas Jaar"
      :year 2015
      :id "201"}
     {:genre "Gothic"
      :artistId "403"
      :name "Wish"
      :songCount 12
      :created "2018-01-02T14:29:04.000Z"
      :duration 3976
      :artist "The Cure"
      :year 1992
      :id "685"
      :coverArt "al-685"}
     {:genre "Gothic"
      :artistId "403"
      :name "Show (Live)"
      :songCount 18
      :created "2018-01-02T14:30:03.000Z"
      :duration 5316
      :artist "The Cure"
      :id "698"
      :coverArt "al-698"}
     {:genre "Gothic"
      :artistId "403"
      :name "Mixed Up"
      :songCount 11
      :created "2018-01-02T14:29:43.000Z"
      :duration 4260
      :artist "The Cure"
      :year 1990
      :id "692"
      :coverArt "al-692"}
     {:id "1257"
      :name "Saal"
      :artist "Serengeti"
      :artistId "678"
      :songCount 13
      :duration 2437
      :created "2018-09-20T17:02:50.000Z"
      :year 2013}
     {:genre "Hip Hop"
      :artistId "204"
      :name "Leaders Of The Brew School"
      :songCount 16
      :created "2017-06-28T18:45:16.000Z"
      :duration 2214
      :artist "Betty Ford Boys"
      :year 2013
      :id "331"}
     {:id "202"
      :name "Sirens"
      :artist "Nicolas Jaar"
      :artistId "117"
      :songCount 7
      :duration 2841
      :created "2017-06-28T20:04:34.000Z"
      :year 2016}
     {:genre "techno"
      :artistId "682"
      :name "Piñata"
      :songCount 21
      :created "2018-10-09T15:30:48.000Z"
      :duration 3963
      :artist "Freddie Gibbs & Madlib"
      :year 2014
      :id "1261"
      :coverArt "al-1261"}
     {:genre "electronic"
      :artistId "681"
      :name "We Must Become the Pitiless Censors of Ourselves"
      :songCount 11
      :created "2018-10-08T17:21:47.000Z"
      :duration 1916
      :artist "John Maus"
      :year 2011
      :id "1260"
      :coverArt "al-1260"}
     {:artistId "514"
      :name "Time"
      :songCount 1
      :created "2017-07-24T13:19:05.000Z"
      :duration 247
      :artist "Lokke"
      :year 2015
      :id "923"
      :coverArt "al-923"}
     {:genre "jazz"
      :artistId "680"
      :name "These Things Take Time"
      :songCount 13
      :created "2018-10-08T17:21:09.000Z"
      :duration 3013
      :artist "Molly Nilsson"
      :year 2008
      :id "1259"
      :coverArt "al-1259"}
     {:artistId "463"
      :name "Songs of Love and Hate"
      :songCount 4
      :created "2018-01-16T11:13:54.000Z"
      :duration 1273
      :artist "Leonard Cohen"
      :year 1970
      :id "829"
      :coverArt "al-829"}
     {:artistId "187"
      :name "Vacation EP"
      :songCount 7
      :created "2017-06-28T20:19:17.000Z"
      :duration 1902
      :artist "Shlohmo"
      :year 2012
      :id "305"
      :coverArt "al-305"}
     {:genre "Electronic"
      :artistId "187"
      :name "Vacation (Remixes)"
      :songCount 6
      :created "2017-06-28T20:19:23.000Z"
      :duration 3559
      :artist "Shlohmo"
      :year 2012
      :id "303"}
     {:genre "WeDidIt"
      :artistId "302"
      :name "Salvation Remixes"
      :songCount 3
      :created "2017-06-28T20:14:04.000Z"
      :duration 739
      :artist "Purple"
      :year 2013
      :id "525"
      :coverArt "al-525"}
     {:genre "Alternative Rock / Indie Rock"
      :artistId "16"
      :name "Sleeping With Ghosts"
      :songCount 22
      :created "2017-11-06T20:39:23.000Z"
      :duration 5232
      :artist "Placebo"
      :year 2003
      :id "38"
      :coverArt "al-38"}
     {:genre "Funk/Hip-Hop"
      :artistId "198"
      :name "Looking For the Perfect Beat"
      :songCount 13
      :created "2017-06-28T18:36:47.000Z"
      :duration 4521
      :artist "Afrika Bambaataa"
      :year 2001
      :id "323"
      :coverArt "al-323"}
     {:artistId "103"
      :name "edits & cuts"
      :songCount 14
      :created "2017-06-28T19:37:20.000Z"
      :duration 3550
      :artist "M.Rux"
      :year 2014
      :id "182"
      :coverArt "al-182"}
     {:genre "Techno"
      :artistId "117"
      :name "Marks / Angles"
      :songCount 3
      :created "2017-06-28T20:03:46.000Z"
      :duration 1000
      :artist "Nicolas Jaar"
      :year 2010
      :id "196"}
     {:genre "Electronic"
      :artistId "73"
      :name "Don't Break My Love EP"
      :songCount 2
      :created "2017-06-28T20:05:16.000Z"
      :duration 673
      :artist "Nicolas Jaar & Theatre Roosevelt"
      :year 2011
      :id "122"
      :coverArt "al-122"}
     {:genre "Electronic"
      :artistId "233"
      :name "Mother Earth's Plantasia"
      :songCount 10
      :created "2018-05-28T21:31:55.000Z"
      :duration 1837
      :artist "Mort Garson"
      :year 1976
      :id "397"
      :coverArt "al-397"}
     {:genre "Psychedelic Rock"
      :artistId "424"
      :name
      "Nuggets: Original Artyfacts From the First Psychedelic Era, 1965-1968"
      :songCount 27
      :created "2018-02-21T12:01:38.000Z"
      :duration 4614
      :artist "Various Artists"
      :year 1998
      :id "743"
      :coverArt "al-743"}
     {:genre "Psychedelic Rock"
      :artistId "37"
      :name "Phluph"
      :songCount 10
      :created "2018-03-05T16:31:46.000Z"
      :duration 2182
      :artist "Phluph"
      :year 2001
      :id "64"
      :coverArt "al-64"}
     {:genre "Rock"
      :artistId "305"
      :name "The Best of Talking Heads (Remastered)"
      :songCount 18
      :created "2018-01-22T11:00:50.000Z"
      :duration 4618
      :artist "Talking Heads"
      :year 2004
      :id "529"
      :coverArt "al-529"}
     {:genre "Electronic"
      :artistId "50"
      :name "Divide And Exit"
      :songCount 14
      :created "2018-01-21T14:47:59.000Z"
      :duration 2417
      :artist "Sleaford Mods"
      :year 2014
      :id "86"
      :coverArt "al-86"}
     {:genre "electronic"
      :artistId "349"
      :name "Fade to Grey: The Best of Visage"
      :songCount 12
      :created "2018-08-29T13:01:26.000Z"
      :duration 2757
      :artist "Visage"
      :year 1993
      :id "1234"}
     {:genre "electronic"
      :artistId "334"
      :name "Hounds of Love"
      :songCount 18
      :created "2018-08-29T13:00:32.000Z"
      :duration 4419
      :artist "Kate Bush"
      :year 1997
      :id "1215"
      :coverArt "al-1215"}
     {:genre "Psychedelic"
      :artistId "424"
      :name
      "Forge Your Own Chains: Heavy Psychedelic Ballads and Dirges 1968-1974"
      :songCount 15
      :created "2018-01-27T12:23:47.000Z"
      :duration 4241
      :artist "Various Artists"
      :id "742"
      :coverArt "al-742"}
     {:genre "Live Archive"
      :artistId "141"
      :name "2017-08-28 Rough Trade NYC, Brooklyn, NY"
      :songCount 4
      :created "2018-01-19T23:07:20.000Z"
      :duration 2483
      :artist "Sunburned Hand of the Man"
      :year 2017
      :id "242"
      :coverArt "al-242"}
     {:genre "electronic"
      :artistId "236"
      :name "Knock Knock"
      :songCount 16
      :created "2018-06-09T23:04:20.000Z"
      :duration 4710
      :artist "DJ Koze"
      :year 2018
      :id "401"
      :coverArt "al-401"}
     {:genre "Nintendocore"
      :artistId "306"
      :name "Gymnasiastik mit Antitainment"
      :songCount 6
      :created "2017-06-28T18:39:58.000Z"
      :duration 795
      :artist "Antitainment"
      :year 2004
      :id "533"}
     {:genre "Electronic"
      :artistId "584"
      :name "Amok"
      :songCount 9
      :created "2017-06-28T18:41:30.000Z"
      :duration 2681
      :artist "Atoms for Peace"
      :year 2013
      :id "1023"
      :coverArt "al-1023"}
     {:artistId "26"
      :name "TamponTango I"
      :songCount 3
      :created "2017-11-23T23:18:43.000Z"
      :duration 851
      :artist "Diederdas"
      :year 2017
      :id "51"
      :coverArt "al-51"}
     {:genre "electronic"
      :artistId "679"
      :name "Heaven and Earth"
      :songCount 16
      :created "2018-09-20T22:07:23.000Z"
      :duration 8672
      :artist "Kamasi Washington"
      :year 2018
      :id "1258"
      :coverArt "al-1258"}
     {:genre "rhy"
      :artistId "661"
      :name "Yawn Zen"
      :songCount 12
      :created "2018-08-21T21:36:43.000Z"
      :duration 1883
      :artist "Mndsgn"
      :year 2014
      :id "1200"
      :coverArt "al-1200"}
     {:genre "Rap"
      :artistId "677"
      :name "Elephant Eyelash"
      :songCount 12
      :created "2018-09-20T17:02:08.000Z"
      :duration 2478
      :artist "Why?"
      :year 2005
      :id "1256"}
     {:genre "Electronic"
      :artistId "41"
      :name "Immunity"
      :songCount 8
      :created "2017-06-28T19:28:24.000Z"
      :duration 3604
      :artist "Jon Hopkins"
      :year 2013
      :id "104"
      :coverArt "al-104"}
     {:genre "IDM / Trip-Hop / Experimental"
      :artistId "454"
      :name "New Energy"
      :songCount 14
      :created "2017-11-25T19:44:56.000Z"
      :duration 3381
      :artist "Four Tet"
      :year 2017
      :id "800"
      :coverArt "al-800"}
     {:genre "Electronic"
      :artistId "633"
      :name "ƒIN (Special Edition)"
      :songCount 20
      :created "2017-06-28T19:26:41.000Z"
      :duration 5822
      :artist "John Talabot"
      :year 2012
      :id "1159"}
     {:artistId "412"
      :name "A Moot Point"
      :songCount 2
      :created "2017-06-28T18:32:24.000Z"
      :duration 857
      :artist "Pional"
      :year 2010
      :id "719"
      :coverArt "al-719"}
     {:id "740"
      :name "KR Family EP, Pt. 1"
      :artist "Peter Power"
      :artistId "230"
      :coverArt "al-740"
      :songCount 3
      :duration 1333
      :created "2017-06-28T20:31:06.000Z"}
     {:genre "House"
      :artistId "482"
      :name "Busy Days For Fools"
      :songCount 11
      :created "2017-06-28T19:35:05.000Z"
      :duration 3238
      :artist "Lee Burton"
      :year 2012
      :id "866"
      :coverArt "al-866"}
     {:id "851"
      :name "Ry & Frank Wiedemann"
      :artist "Ry & Frank Wiedemann"
      :artistId "472"
      :songCount 1
      :duration 485
      :created "2017-06-28T18:34:23.000Z"}
     {:genre "Electronic"
      :artistId "58"
      :name "Deep Cuts"
      :songCount 17
      :created "2017-12-22T08:21:19.000Z"
      :duration 3321
      :artist "The Knife"
      :year 2003
      :id "96"
      :coverArt "al-96"}
     {:artistId "125"
      :name "VIA Remixes"
      :songCount 1
      :created "2017-06-28T18:27:59.000Z"
      :duration 362
      :artist "Andi Otto"
      :year 2017
      :id "211"
      :coverArt "al-211"}
     {:artistId "626"
      :name "Hummingbird / Milk & Honey"
      :songCount 2
      :created "2017-11-23T21:27:00.000Z"
      :duration 303
      :artist "Luca Nieri"
      :year 2016
      :id "1150"
      :coverArt "al-1150"}
     {:genre "Electronic"
      :artistId "434"
      :name "Mercy Street"
      :songCount 2
      :created "2017-12-22T08:18:55.000Z"
      :duration 568
      :artist "Fever Ray"
      :year 2010
      :id "762"
      :coverArt "al-762"}
     {:artistId "43"
      :name "2012-2017"
      :songCount 11
      :created "2018-03-06T15:51:42.000Z"
      :duration 3998
      :artist "A.A.L."
      :year 2018
      :id "73"
      :coverArt "al-73"}
     {:genre "New Wave Music"
      :artistId "337"
      :name "Liaisons dangereuses"
      :songCount 10
      :created "2018-08-29T13:00:42.000Z"
      :duration 2392
      :artist "Liaisons Dangereuses"
      :year 1985
      :id "1216"}
     {:genre "Electro"
      :artistId "434"
      :name "Fever Ray"
      :songCount 12
      :created "2017-12-22T08:19:04.000Z"
      :duration 3380
      :artist "Fever Ray"
      :year 2009
      :id "765"}
     {:id "621"
      :name "RSS Disco"
      :artist "RSS Disco"
      :artistId "358"
      :songCount 2
      :duration 841
      :created "2018-04-25T10:11:14.000Z"}
     {:genre "House"
      :artistId "358"
      :name "Very"
      :songCount 3
      :created "2017-06-28T20:17:12.000Z"
      :duration 1339
      :artist "RSS Disco"
      :year 2012
      :id "624"
      :coverArt "al-624"}
     {:genre "Disco"
      :artistId "619"
      :name "Sir John"
      :songCount 1
      :created "2018-03-12T20:21:14.000Z"
      :duration 419
      :artist "White Elephant"
      :year 2011
      :id "1134"
      :coverArt "al-1134"}
     {:genre "House"
      :artistId "434"
      :name "Sidetracked"
      :songCount 1
      :created "2017-06-28T18:20:10.000Z"
      :duration 270
      :artist "Fever Ray"
      :year 2012
      :id "920"
      :coverArt "al-920"}
     {:genre "Electronic"
      :artistId "58"
      :name "Hannah Med H Soundtrack"
      :songCount 16
      :created "2017-12-22T08:21:33.000Z"
      :duration 2307
      :artist "The Knife"
      :year 2003
      :id "97"
      :coverArt "al-97"}
     {:genre "Alternative Rock"
      :artistId "478"
      :name "6 Feet Beneath the Moon"
      :songCount 14
      :created "2017-09-08T17:37:16.000Z"
      :duration 3136
      :artist "King Krule"
      :year 2013
      :id "859"
      :coverArt "al-859"}
     {:artistId "103"
      :name "Joga / Crazy Junker 7\""
      :songCount 2
      :created "2017-06-28T19:37:31.000Z"
      :duration 442
      :artist "M.Rux"
      :year 2014
      :id "177"
      :coverArt "al-177"}
     {:genre "House"
      :artistId "267"
      :name "Carat EP"
      :songCount 5
      :created "2017-06-28T20:08:58.000Z"
      :duration 2080
      :artist "Nu"
      :year 2013
      :id "467"}
     {:artistId "419"
      :name "On Claws (reissue)"
      :songCount 1
      :created "2017-07-24T13:48:20.000Z"
      :duration 176
      :artist "I am Oak"
      :year 2013
      :id "733"
      :coverArt "al-733"}
     {:genre "Indie Dance / Nu Disco"
      :artistId "214"
      :name "Thinking Allowed"
      :songCount 1
      :created "2018-01-02T18:54:41.000Z"
      :duration 430
      :artist "Tornado Wallace"
      :year 2013
      :id "354"
      :coverArt "al-354"}
     {:artistId "629"
      :name "V.I.C.T.O.R"
      :songCount 1
      :created "2017-06-28T18:25:45.000Z"
      :duration 279
      :artist "Golden Bug"
      :year 2016
      :id "1153"
      :coverArt "al-1153"}
     {:genre "Avant-Garde"
      :artistId "256"
      :name "Ende Neu"
      :songCount 9
      :created "2017-06-28T19:09:43.000Z"
      :duration 2693
      :artist "Einstürzende Neubauten"
      :year 1998
      :id "426"
      :coverArt "al-426"}
     {:genre "House"
      :artistId "245"
      :name "Visibles"
      :songCount 4
      :created "2017-06-28T18:57:22.000Z"
      :duration 1556
      :artist "Constantijn Lange"
      :year 2014
      :id "413"
      :coverArt "al-413"}
     {:artistId "245"
      :name "Orange Atlas"
      :songCount 5
      :created "2017-06-28T18:57:08.000Z"
      :duration 2171
      :artist "Constantijn Lange"
      :year 2013
      :id "412"
      :coverArt "al-412"}
     {:artistId "146"
      :name "Mapping The Futures Gone By"
      :songCount 7
      :created "2017-06-28T18:57:28.000Z"
      :duration 1536
      :artist "CONTACT FIELD ORCHESTRA"
      :year 2015
      :id "247"
      :coverArt "al-247"}
     {:genre "electronic"
      :artistId "253"
      :name "It's Album Time"
      :songCount 12
      :created "2018-09-04T14:25:00.000Z"
      :duration 3555
      :artist "Todd Terje"
      :year 2014
      :id "1254"
      :coverArt "al-1254"}]}
   ["getAlbumList2" {:type "recent", :size 100, :offset 40}]
   {:album
    [{:artistId "96"
      :name "www.soundcloud.com/rampue"
      :songCount 1
      :created "2017-06-28T19:31:41.000Z"
      :duration 424
      :artist "Klima"
      :year 2013
      :id "166"
      :coverArt "al-166"}
     {:artistId "463"
      :name "soundcloud.com/rampue"
      :songCount 1
      :created "2017-06-28T19:35:30.000Z"
      :duration 523
      :artist "Leonard Cohen"
      :year 2014
      :id "831"
      :coverArt "al-831"}
     {:artistId "463"
      :name "The Future"
      :songCount 9
      :created "2018-01-16T11:14:41.000Z"
      :duration 3579
      :artist "Leonard Cohen"
      :year 2012
      :id "821"
      :coverArt "al-821"}
     {:id "1001"
      :name "Lonski & Classen"
      :artist "Lonski & Classen"
      :artistId "566"
      :coverArt "al-1001"
      :songCount 1
      :duration 248
      :created "2017-06-28T20:07:24.000Z"}
     {:genre "Podcast"
      :artistId "199"
      :name "Waterkant Souvenirs Podcast"
      :songCount 1
      :created "2017-06-28T20:00:25.000Z"
      :duration 5341
      :artist "Mira"
      :year 2012
      :id "325"}
     {:id "324"
      :name "Familiar Forest Festival 2012"
      :artist "Mira"
      :artistId "199"
      :songCount 1
      :duration 6695
      :created "2017-06-28T20:00:35.000Z"
      :year 2012}
     {:genre "Ambient"
      :artistId "188"
      :name "We're New Here"
      :songCount 13
      :created "2017-06-28T19:18:06.000Z"
      :duration 2135
      :artist "Gil Scott-Heron and Jamie xx"
      :year 2011
      :id "310"
      :coverArt "al-310"}
     {:genre "Gothic"
      :artistId "403"
      :name "Galore : The Singles 87 - 97"
      :songCount 18
      :created "2017-11-06T20:51:35.000Z"
      :duration 4369
      :artist "The Cure"
      :year 1997
      :id "684"
      :coverArt "al-684"}
     {:genre "IDM"
      :artistId "333"
      :name "Remixes Compiled"
      :songCount 12
      :created "2017-06-28T20:22:43.000Z"
      :duration 3233
      :artist "Telefon Tel Aviv"
      :year 2007
      :id "723"
      :coverArt "al-723"}
     {:artistId "230"
      :name "Ufordian Edits"
      :songCount 1
      :created "2018-02-19T22:55:59.000Z"
      :duration 331
      :artist "Peter Power"
      :year 2015
      :id "393"
      :coverArt "al-393"}
     {:genre "Other"
      :artistId "528"
      :name "Dream Runner EP"
      :songCount 6
      :created "2017-06-28T18:39:24.000Z"
      :duration 899
      :artist "Annu"
      :year 2009
      :id "948"}
     {:genre "Techno"
      :artistId "75"
      :name "Unknown"
      :songCount 8
      :created "2017-06-28T20:17:47.000Z"
      :duration 2841
      :artist "Saschienne"
      :year 2012
      :id "124"
      :coverArt "al-124"}
     {:genre "Nintendocore"
      :artistId "306"
      :name "Nach der Kippe Pogo!?"
      :songCount 11
      :created "2017-06-28T18:40:09.000Z"
      :duration 1508
      :artist "Antitainment"
      :year 2007
      :id "532"
      :coverArt "al-532"}
     {:genre "Electronic"
      :artistId "206"
      :name "Swim"
      :songCount 9
      :created "2017-11-25T20:06:58.000Z"
      :duration 2596
      :artist "Caribou"
      :year 2010
      :id "339"
      :coverArt "al-339"}
     {:genre "trance"
      :artistId "117"
      :name "Nymphs III"
      :songCount 2
      :created "2017-06-28T20:04:17.000Z"
      :duration 1080
      :artist "Nicolas Jaar"
      :year 2015
      :id "201"}
     {:genre "Gothic"
      :artistId "403"
      :name "Wish"
      :songCount 12
      :created "2018-01-02T14:29:04.000Z"
      :duration 3976
      :artist "The Cure"
      :year 1992
      :id "685"
      :coverArt "al-685"}
     {:genre "Gothic"
      :artistId "403"
      :name "Show (Live)"
      :songCount 18
      :created "2018-01-02T14:30:03.000Z"
      :duration 5316
      :artist "The Cure"
      :id "698"
      :coverArt "al-698"}
     {:genre "Gothic"
      :artistId "403"
      :name "Mixed Up"
      :songCount 11
      :created "2018-01-02T14:29:43.000Z"
      :duration 4260
      :artist "The Cure"
      :year 1990
      :id "692"
      :coverArt "al-692"}
     {:id "1257"
      :name "Saal"
      :artist "Serengeti"
      :artistId "678"
      :songCount 13
      :duration 2437
      :created "2018-09-20T17:02:50.000Z"
      :year 2013}
     {:genre "Hip Hop"
      :artistId "204"
      :name "Leaders Of The Brew School"
      :songCount 16
      :created "2017-06-28T18:45:16.000Z"
      :duration 2214
      :artist "Betty Ford Boys"
      :year 2013
      :id "331"}
     {:id "202"
      :name "Sirens"
      :artist "Nicolas Jaar"
      :artistId "117"
      :songCount 7
      :duration 2841
      :created "2017-06-28T20:04:34.000Z"
      :year 2016}
     {:genre "techno"
      :artistId "682"
      :name "Piñata"
      :songCount 21
      :created "2018-10-09T15:30:48.000Z"
      :duration 3963
      :artist "Freddie Gibbs & Madlib"
      :year 2014
      :id "1261"
      :coverArt "al-1261"}
     {:genre "electronic"
      :artistId "681"
      :name "We Must Become the Pitiless Censors of Ourselves"
      :songCount 11
      :created "2018-10-08T17:21:47.000Z"
      :duration 1916
      :artist "John Maus"
      :year 2011
      :id "1260"
      :coverArt "al-1260"}
     {:artistId "514"
      :name "Time"
      :songCount 1
      :created "2017-07-24T13:19:05.000Z"
      :duration 247
      :artist "Lokke"
      :year 2015
      :id "923"
      :coverArt "al-923"}
     {:genre "jazz"
      :artistId "680"
      :name "These Things Take Time"
      :songCount 13
      :created "2018-10-08T17:21:09.000Z"
      :duration 3013
      :artist "Molly Nilsson"
      :year 2008
      :id "1259"
      :coverArt "al-1259"}
     {:artistId "463"
      :name "Songs of Love and Hate"
      :songCount 4
      :created "2018-01-16T11:13:54.000Z"
      :duration 1273
      :artist "Leonard Cohen"
      :year 1970
      :id "829"
      :coverArt "al-829"}
     {:artistId "187"
      :name "Vacation EP"
      :songCount 7
      :created "2017-06-28T20:19:17.000Z"
      :duration 1902
      :artist "Shlohmo"
      :year 2012
      :id "305"
      :coverArt "al-305"}
     {:genre "Electronic"
      :artistId "187"
      :name "Vacation (Remixes)"
      :songCount 6
      :created "2017-06-28T20:19:23.000Z"
      :duration 3559
      :artist "Shlohmo"
      :year 2012
      :id "303"}
     {:genre "WeDidIt"
      :artistId "302"
      :name "Salvation Remixes"
      :songCount 3
      :created "2017-06-28T20:14:04.000Z"
      :duration 739
      :artist "Purple"
      :year 2013
      :id "525"
      :coverArt "al-525"}
     {:genre "Alternative Rock / Indie Rock"
      :artistId "16"
      :name "Sleeping With Ghosts"
      :songCount 22
      :created "2017-11-06T20:39:23.000Z"
      :duration 5232
      :artist "Placebo"
      :year 2003
      :id "38"
      :coverArt "al-38"}
     {:genre "Funk/Hip-Hop"
      :artistId "198"
      :name "Looking For the Perfect Beat"
      :songCount 13
      :created "2017-06-28T18:36:47.000Z"
      :duration 4521
      :artist "Afrika Bambaataa"
      :year 2001
      :id "323"
      :coverArt "al-323"}
     {:artistId "103"
      :name "edits & cuts"
      :songCount 14
      :created "2017-06-28T19:37:20.000Z"
      :duration 3550
      :artist "M.Rux"
      :year 2014
      :id "182"
      :coverArt "al-182"}
     {:genre "Techno"
      :artistId "117"
      :name "Marks / Angles"
      :songCount 3
      :created "2017-06-28T20:03:46.000Z"
      :duration 1000
      :artist "Nicolas Jaar"
      :year 2010
      :id "196"}
     {:genre "Electronic"
      :artistId "73"
      :name "Don't Break My Love EP"
      :songCount 2
      :created "2017-06-28T20:05:16.000Z"
      :duration 673
      :artist "Nicolas Jaar & Theatre Roosevelt"
      :year 2011
      :id "122"
      :coverArt "al-122"}
     {:genre "Electronic"
      :artistId "233"
      :name "Mother Earth's Plantasia"
      :songCount 10
      :created "2018-05-28T21:31:55.000Z"
      :duration 1837
      :artist "Mort Garson"
      :year 1976
      :id "397"
      :coverArt "al-397"}
     {:genre "Psychedelic Rock"
      :artistId "424"
      :name
      "Nuggets: Original Artyfacts From the First Psychedelic Era, 1965-1968"
      :songCount 27
      :created "2018-02-21T12:01:38.000Z"
      :duration 4614
      :artist "Various Artists"
      :year 1998
      :id "743"
      :coverArt "al-743"}
     {:genre "Psychedelic Rock"
      :artistId "37"
      :name "Phluph"
      :songCount 10
      :created "2018-03-05T16:31:46.000Z"
      :duration 2182
      :artist "Phluph"
      :year 2001
      :id "64"
      :coverArt "al-64"}
     {:genre "Rock"
      :artistId "305"
      :name "The Best of Talking Heads (Remastered)"
      :songCount 18
      :created "2018-01-22T11:00:50.000Z"
      :duration 4618
      :artist "Talking Heads"
      :year 2004
      :id "529"
      :coverArt "al-529"}
     {:genre "Electronic"
      :artistId "50"
      :name "Divide And Exit"
      :songCount 14
      :created "2018-01-21T14:47:59.000Z"
      :duration 2417
      :artist "Sleaford Mods"
      :year 2014
      :id "86"
      :coverArt "al-86"}
     {:genre "electronic"
      :artistId "349"
      :name "Fade to Grey: The Best of Visage"
      :songCount 12
      :created "2018-08-29T13:01:26.000Z"
      :duration 2757
      :artist "Visage"
      :year 1993
      :id "1234"}
     {:genre "electronic"
      :artistId "334"
      :name "Hounds of Love"
      :songCount 18
      :created "2018-08-29T13:00:32.000Z"
      :duration 4419
      :artist "Kate Bush"
      :year 1997
      :id "1215"
      :coverArt "al-1215"}
     {:genre "Psychedelic"
      :artistId "424"
      :name
      "Forge Your Own Chains: Heavy Psychedelic Ballads and Dirges 1968-1974"
      :songCount 15
      :created "2018-01-27T12:23:47.000Z"
      :duration 4241
      :artist "Various Artists"
      :id "742"
      :coverArt "al-742"}
     {:genre "Live Archive"
      :artistId "141"
      :name "2017-08-28 Rough Trade NYC, Brooklyn, NY"
      :songCount 4
      :created "2018-01-19T23:07:20.000Z"
      :duration 2483
      :artist "Sunburned Hand of the Man"
      :year 2017
      :id "242"
      :coverArt "al-242"}
     {:genre "electronic"
      :artistId "236"
      :name "Knock Knock"
      :songCount 16
      :created "2018-06-09T23:04:20.000Z"
      :duration 4710
      :artist "DJ Koze"
      :year 2018
      :id "401"
      :coverArt "al-401"}
     {:genre "Nintendocore"
      :artistId "306"
      :name "Gymnasiastik mit Antitainment"
      :songCount 6
      :created "2017-06-28T18:39:58.000Z"
      :duration 795
      :artist "Antitainment"
      :year 2004
      :id "533"}
     {:genre "Electronic"
      :artistId "584"
      :name "Amok"
      :songCount 9
      :created "2017-06-28T18:41:30.000Z"
      :duration 2681
      :artist "Atoms for Peace"
      :year 2013
      :id "1023"
      :coverArt "al-1023"}
     {:artistId "26"
      :name "TamponTango I"
      :songCount 3
      :created "2017-11-23T23:18:43.000Z"
      :duration 851
      :artist "Diederdas"
      :year 2017
      :id "51"
      :coverArt "al-51"}
     {:genre "electronic"
      :artistId "679"
      :name "Heaven and Earth"
      :songCount 16
      :created "2018-09-20T22:07:23.000Z"
      :duration 8672
      :artist "Kamasi Washington"
      :year 2018
      :id "1258"
      :coverArt "al-1258"}
     {:genre "rhy"
      :artistId "661"
      :name "Yawn Zen"
      :songCount 12
      :created "2018-08-21T21:36:43.000Z"
      :duration 1883
      :artist "Mndsgn"
      :year 2014
      :id "1200"
      :coverArt "al-1200"}
     {:genre "Rap"
      :artistId "677"
      :name "Elephant Eyelash"
      :songCount 12
      :created "2018-09-20T17:02:08.000Z"
      :duration 2478
      :artist "Why?"
      :year 2005
      :id "1256"}
     {:genre "Electronic"
      :artistId "41"
      :name "Immunity"
      :songCount 8
      :created "2017-06-28T19:28:24.000Z"
      :duration 3604
      :artist "Jon Hopkins"
      :year 2013
      :id "104"
      :coverArt "al-104"}
     {:genre "IDM / Trip-Hop / Experimental"
      :artistId "454"
      :name "New Energy"
      :songCount 14
      :created "2017-11-25T19:44:56.000Z"
      :duration 3381
      :artist "Four Tet"
      :year 2017
      :id "800"
      :coverArt "al-800"}
     {:genre "Electronic"
      :artistId "633"
      :name "ƒIN (Special Edition)"
      :songCount 20
      :created "2017-06-28T19:26:41.000Z"
      :duration 5822
      :artist "John Talabot"
      :year 2012
      :id "1159"}
     {:artistId "412"
      :name "A Moot Point"
      :songCount 2
      :created "2017-06-28T18:32:24.000Z"
      :duration 857
      :artist "Pional"
      :year 2010
      :id "719"
      :coverArt "al-719"}
     {:id "740"
      :name "KR Family EP, Pt. 1"
      :artist "Peter Power"
      :artistId "230"
      :coverArt "al-740"
      :songCount 3
      :duration 1333
      :created "2017-06-28T20:31:06.000Z"}
     {:genre "House"
      :artistId "482"
      :name "Busy Days For Fools"
      :songCount 11
      :created "2017-06-28T19:35:05.000Z"
      :duration 3238
      :artist "Lee Burton"
      :year 2012
      :id "866"
      :coverArt "al-866"}
     {:id "851"
      :name "Ry & Frank Wiedemann"
      :artist "Ry & Frank Wiedemann"
      :artistId "472"
      :songCount 1
      :duration 485
      :created "2017-06-28T18:34:23.000Z"}
     {:genre "Electronic"
      :artistId "58"
      :name "Deep Cuts"
      :songCount 17
      :created "2017-12-22T08:21:19.000Z"
      :duration 3321
      :artist "The Knife"
      :year 2003
      :id "96"
      :coverArt "al-96"}
     {:artistId "125"
      :name "VIA Remixes"
      :songCount 1
      :created "2017-06-28T18:27:59.000Z"
      :duration 362
      :artist "Andi Otto"
      :year 2017
      :id "211"
      :coverArt "al-211"}
     {:artistId "626"
      :name "Hummingbird / Milk & Honey"
      :songCount 2
      :created "2017-11-23T21:27:00.000Z"
      :duration 303
      :artist "Luca Nieri"
      :year 2016
      :id "1150"
      :coverArt "al-1150"}
     {:genre "Electronic"
      :artistId "434"
      :name "Mercy Street"
      :songCount 2
      :created "2017-12-22T08:18:55.000Z"
      :duration 568
      :artist "Fever Ray"
      :year 2010
      :id "762"
      :coverArt "al-762"}
     {:artistId "43"
      :name "2012-2017"
      :songCount 11
      :created "2018-03-06T15:51:42.000Z"
      :duration 3998
      :artist "A.A.L."
      :year 2018
      :id "73"
      :coverArt "al-73"}
     {:genre "New Wave Music"
      :artistId "337"
      :name "Liaisons dangereuses"
      :songCount 10
      :created "2018-08-29T13:00:42.000Z"
      :duration 2392
      :artist "Liaisons Dangereuses"
      :year 1985
      :id "1216"}
     {:genre "Electro"
      :artistId "434"
      :name "Fever Ray"
      :songCount 12
      :created "2017-12-22T08:19:04.000Z"
      :duration 3380
      :artist "Fever Ray"
      :year 2009
      :id "765"}
     {:id "621"
      :name "RSS Disco"
      :artist "RSS Disco"
      :artistId "358"
      :songCount 2
      :duration 841
      :created "2018-04-25T10:11:14.000Z"}
     {:genre "House"
      :artistId "358"
      :name "Very"
      :songCount 3
      :created "2017-06-28T20:17:12.000Z"
      :duration 1339
      :artist "RSS Disco"
      :year 2012
      :id "624"
      :coverArt "al-624"}
     {:genre "Disco"
      :artistId "619"
      :name "Sir John"
      :songCount 1
      :created "2018-03-12T20:21:14.000Z"
      :duration 419
      :artist "White Elephant"
      :year 2011
      :id "1134"
      :coverArt "al-1134"}
     {:genre "House"
      :artistId "434"
      :name "Sidetracked"
      :songCount 1
      :created "2017-06-28T18:20:10.000Z"
      :duration 270
      :artist "Fever Ray"
      :year 2012
      :id "920"
      :coverArt "al-920"}
     {:genre "Electronic"
      :artistId "58"
      :name "Hannah Med H Soundtrack"
      :songCount 16
      :created "2017-12-22T08:21:33.000Z"
      :duration 2307
      :artist "The Knife"
      :year 2003
      :id "97"
      :coverArt "al-97"}
     {:genre "Alternative Rock"
      :artistId "478"
      :name "6 Feet Beneath the Moon"
      :songCount 14
      :created "2017-09-08T17:37:16.000Z"
      :duration 3136
      :artist "King Krule"
      :year 2013
      :id "859"
      :coverArt "al-859"}
     {:artistId "103"
      :name "Joga / Crazy Junker 7\""
      :songCount 2
      :created "2017-06-28T19:37:31.000Z"
      :duration 442
      :artist "M.Rux"
      :year 2014
      :id "177"
      :coverArt "al-177"}
     {:genre "House"
      :artistId "267"
      :name "Carat EP"
      :songCount 5
      :created "2017-06-28T20:08:58.000Z"
      :duration 2080
      :artist "Nu"
      :year 2013
      :id "467"}
     {:artistId "419"
      :name "On Claws (reissue)"
      :songCount 1
      :created "2017-07-24T13:48:20.000Z"
      :duration 176
      :artist "I am Oak"
      :year 2013
      :id "733"
      :coverArt "al-733"}
     {:genre "Indie Dance / Nu Disco"
      :artistId "214"
      :name "Thinking Allowed"
      :songCount 1
      :created "2018-01-02T18:54:41.000Z"
      :duration 430
      :artist "Tornado Wallace"
      :year 2013
      :id "354"
      :coverArt "al-354"}
     {:artistId "629"
      :name "V.I.C.T.O.R"
      :songCount 1
      :created "2017-06-28T18:25:45.000Z"
      :duration 279
      :artist "Golden Bug"
      :year 2016
      :id "1153"
      :coverArt "al-1153"}
     {:genre "Avant-Garde"
      :artistId "256"
      :name "Ende Neu"
      :songCount 9
      :created "2017-06-28T19:09:43.000Z"
      :duration 2693
      :artist "Einstürzende Neubauten"
      :year 1998
      :id "426"
      :coverArt "al-426"}
     {:genre "House"
      :artistId "245"
      :name "Visibles"
      :songCount 4
      :created "2017-06-28T18:57:22.000Z"
      :duration 1556
      :artist "Constantijn Lange"
      :year 2014
      :id "413"
      :coverArt "al-413"}
     {:artistId "245"
      :name "Orange Atlas"
      :songCount 5
      :created "2017-06-28T18:57:08.000Z"
      :duration 2171
      :artist "Constantijn Lange"
      :year 2013
      :id "412"
      :coverArt "al-412"}
     {:artistId "146"
      :name "Mapping The Futures Gone By"
      :songCount 7
      :created "2017-06-28T18:57:28.000Z"
      :duration 1536
      :artist "CONTACT FIELD ORCHESTRA"
      :year 2015
      :id "247"
      :coverArt "al-247"}
     {:genre "electronic"
      :artistId "253"
      :name "It's Album Time"
      :songCount 12
      :created "2018-09-04T14:25:00.000Z"
      :duration 3555
      :artist "Todd Terje"
      :year 2014
      :id "1254"
      :coverArt "al-1254"}
     {:genre "electronic"
      :artistId "676"
      :name "The Big Cover-Up"
      :songCount 8
      :created "2018-09-04T14:44:38.000Z"
      :duration 3130
      :artist "Todd Terje & The Olsens"
      :year 2016
      :id "1255"
      :coverArt "al-1255"}
     {:genre "electronic"
      :artistId "424"
      :name "I-Robots: Italo Electro Disco Underground Classics"
      :songCount 13
      :created "2018-08-29T13:01:11.000Z"
      :duration 4797
      :artist "Various Artists"
      :year 2004
      :id "1217"}
     {:genre "Electronic"
      :artistId "497"
      :name "Creature Dreams"
      :songCount 7
      :created "2017-06-28T20:27:36.000Z"
      :duration 1709
      :artist "TOKiMONSTA"
      :year 2011
      :id "897"}
     {:genre "Other"
      :artistId "466"
      :name "Brighton Beach (Freddie Joachim Remix)"
      :songCount 1
      :created "2017-06-28T18:34:34.000Z"
      :duration 187
      :artist "Télépopmusik"
      :year 2011
      :id "838"}
     {:genre "Hip-Hop"
      :artistId "234"
      :name "Viktor Vaughn - Vaudeville Villain"
      :songCount 30
      :created "2017-06-28T20:45:05.000Z"
      :duration 6039
      :artist "MF Doom"
      :year 2012
      :id "1079"
      :coverArt "al-1079"}
     {:genre "Hip-Hop"
      :artistId "234"
      :name "King Geedorah - Take Me To Your Leader"
      :songCount 13
      :created "2017-06-28T20:44:03.000Z"
      :duration 2514
      :artist "MF Doom"
      :year 2003
      :id "1078"
      :coverArt "al-1078"}
     {:genre "electronic"
      :artistId "667"
      :name "Solid State Survivor"
      :songCount 8
      :created "2018-08-29T13:02:20.000Z"
      :duration 1921
      :artist "Yellow Magic Orchestra"
      :year 1979
      :id "1231"}
     {:genre "electronic"
      :artistId "666"
      :name "Technodon"
      :songCount 12
      :created "2018-08-29T13:02:40.000Z"
      :duration 3806
      :artist "Y̶M̶O̶"
      :year 1993
      :id "1224"}
     {:genre "Alternative Hip Hop"
      :artistId "669"
      :name "Unicron"
      :songCount 6
      :created "2018-08-29T13:45:33.000Z"
      :duration 887
      :artist "MF DOOM & Trunks"
      :year 2008
      :id "1235"
      :coverArt "al-1235"}
     {:genre "Alternative Hip Hop"
      :artistId "650"
      :name "Special Herbs, Volume 5 & 6"
      :songCount 13
      :created "2018-08-29T13:45:33.000Z"
      :duration 2760
      :artist "Metal Fingers"
      :year 2004
      :id "1248"
      :coverArt "al-1248"}
     {:genre "Alternative Hip Hop"
      :artistId "650"
      :name "Special Herbs, Volume 3 & 4"
      :songCount 16
      :created "2018-08-29T13:45:43.000Z"
      :duration 3054
      :artist "Metal Fingers"
      :year 2003
      :id "1251"
      :coverArt "al-1251"}
     {:genre "electronic"
      :artistId "650"
      :name "Special Herbs, Volume 9 & 0"
      :songCount 13
      :created "2018-08-29T13:45:57.000Z"
      :duration 2751
      :artist "Metal Fingers"
      :year 2005
      :id "1249"
      :coverArt "al-1249"}
     {:genre "electronic"
      :artistId "650"
      :name "Special Herbs, Volume 7 & 8"
      :songCount 13
      :created "2018-08-29T13:46:07.000Z"
      :duration 2680
      :artist "Metal Fingers"
      :year 2004
      :id "1250"
      :coverArt "al-1250"}
     {:genre "raphiphop"
      :artistId "674"
      :name "Key to the Kuffs"
      :songCount 15
      :created "2018-08-29T13:46:12.000Z"
      :duration 2520
      :artist "JJ DOOM"
      :year 2012
      :id "1245"
      :coverArt "al-1245"}
     {:genre "Hip Hop Music"
      :artistId "647"
      :name "The Prof. Meets the Supervillain"
      :songCount 5
      :created "2018-08-29T13:46:19.000Z"
      :duration 829
      :artist "MF DOOM"
      :year 2003
      :id "1244"
      :coverArt "al-1244"}
     {:genre "Hip Hop Music"
      :artistId "647"
      :name "Vomit"
      :songCount 6
      :created "2018-08-29T13:46:24.000Z"
      :duration 1254
      :artist "MF DOOM"
      :year 2006
      :id "1241"
      :coverArt "al-1241"}
     {:genre "Hip-Hop"
      :artistId "670"
      :name "Victory Laps"
      :songCount 6
      :created "2018-08-29T13:46:34.000Z"
      :duration 1026
      :artist "DOOMSTARKS"
      :year 2011
      :id "1237"
      :coverArt "al-1237"}
     {:genre "rock"
      :artistId "672"
      :name "(VV:2) Venomous Villain"
      :songCount 12
      :created "2018-08-29T13:46:36.000Z"
      :duration 1976
      :artist "Viktor Vaughn"
      :year 2004
      :id "1242"
      :coverArt "al-1242"}
     {:genre "Hip Hop Music"
      :artistId "671"
      :name "Air"
      :songCount 5
      :created "2018-08-29T13:46:39.000Z"
      :duration 803
      :artist "Dabrye"
      :year 2006
      :id "1238"
      :coverArt "al-1238"}
     {:id "984"
      :name "The Wicker Man"
      :artist "The Wicker Man"
      :artistId "553"
      :coverArt "al-984"
      :songCount 1
      :duration 243
      :created "2017-06-28T20:06:58.000Z"}]}})