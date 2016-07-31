# Utility for comparing language translation keys of BASE_LANG and a given language.
# Usage: perl translation-diff.pl $lang
# Will output a translation-diff.diff file that compares the lang keys of BASE_LANG to $lang.

use constant {
    BASE_LANG => "en_US",
    OUTPUT_FILE => "translation-diff.diff"
};

my $lang = $ARGV[0] or die("Missing language to diff with");

sub lang_file {
    my ($lang) = @_;
    my $filename = "src/main/resources/assets/refinedstorage/lang/" . $lang . ".lang";
    open(my $fh, $filename) or die("Couldn't open $filename");
    return $fh;
}

sub write_keys {
    my ($lang, $lang_keys) = @_;
    my $filename = $lang . ".tmp";
    open(my $fh, '>', $filename) or die("Couldn't open temp file $filename for $lang for writing");
    print $fh $lang_keys;
    close $fh;
    return $filename;
}

sub lang_keys {
    my ($lang_file) = @_;
    my $keys = "";
    while (my $line = <$lang_file>) {
        my @p = split("=", $line);
        $keys .= $p[0] . "\n";
    }
    return $keys;
}

my $f1 = lang_file(BASE_LANG);
my $f2 = lang_file($lang);

my $base_keys = lang_keys($f1);
my $t1 = write_keys(BASE_LANG, $base_keys);

my $lang_keys = lang_keys($f2);
my $t2 = write_keys($lang, $lang_keys);

system("diff -s -y $t1 $t2 > " . OUTPUT_FILE);

unlink($t1, $t2);

close($f1, $f2);
