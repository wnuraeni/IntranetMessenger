-- phpMyAdmin SQL Dump
-- version 4.0.9
-- http://www.phpmyadmin.net
--
-- Inang: 127.0.0.1
-- Waktu pembuatan: 08 Agu 2014 pada 04.14
-- Versi Server: 5.6.14
-- Versi PHP: 5.5.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Basis data: `android_messaging`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `message`
--

CREATE TABLE IF NOT EXISTS `message` (
  `id_message` int(10) NOT NULL AUTO_INCREMENT,
  `sender` varchar(150) NOT NULL,
  `receiver` varchar(150) NOT NULL,
  `date_time` varchar(150) NOT NULL,
  `message` text NOT NULL,
  `status` int(2) NOT NULL,
  PRIMARY KEY (`id_message`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(150) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `status_online` int(2) NOT NULL,
  `picture` varchar(150) NOT NULL DEFAULT '1.jpg',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id`, `username`, `phone`, `status_online`, `picture`) VALUES
(1, 'leno', '08999458254', 0, '1.jpg'),
(2, 'samsung', '081394434558', 0, '2.jpg');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
